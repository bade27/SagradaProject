package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;

import java.util.ArrayList;

public class MatchHandler implements Runnable
{
    private LogFile log;

    //List of active serverPlayer
    private ArrayList<ServerPlayer> player;
    private ArrayList<Thread> threadPlayers;

    //Number of client connected
    private int nConn;
    private TokenTurn tok;
    private Dadiera dices;

    private final static int MAXGIOC = 2;//Da modificare a 4

    public synchronized void run ()
    {
        log = new LogFile("ServerLog");

        acceptConnection();
        initializeWindowPlayers();
        initializePublicObjectiveCards();
        waitInitialition();
        System.out.println(">>>Initialization ended");
        startGame();
    }

    /**
     * Manage the match: checks status, wakes players, and manage match components
     * */
    private void startGame ()
    {
        log.addLog("Game Phase started");
        //clientConnectionUpdateMessage("playing");
        dices.mix(tok.getNumPlayers());
        //tok.setGameStarted(true);
        log.addLog("Dadiera Mixed");

        updateClient();

        while (true)
        {
            synchronized (tok)
            {
                if (tok.isFatalError())
                    closeAllConnection();

                if (tok.isEndRound())
                {
                    dices.mix(tok.getNumPlayers());
                    System.out.println(">>>Dadiera Mixed");
                    log.addLog("Dadiera Mixed");
                }
                updateClient();
                tok.nextTurn();
                tok.notifyAll();

                try {
                    tok.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();
                    closeAllConnection();
                }
            }
        }
    }

    /**
     * Initialize connection with server for each players
     */
    private void acceptConnection()
    {
        System.out.println(">>>server Started");
        log.addLog("server Started");
        nConn = 0;
        threadPlayers = new ArrayList<Thread>();
        player = new ArrayList<ServerPlayer>();
        try
        {
            tok = new TokenTurn();
            dices = new Dadiera();

            //Initialize possible username
            ArrayList possibleUsrs = initializePossibleUsers();

            //progressive is a progressive number of connection created
            int progressive = 0;
            //For each players starts waiting for connection
            for (int i =0 ;i< MAXGIOC;i ++)
            {
                ServerPlayer pl = new ServerPlayer(tok,new ServerModelAdapter(dices),possibleUsrs,log);
                if (pl.initializeCommunication(progressive))
                    player.add(pl);
                else
                    i--;
                int n = checkClientAlive();
                clientConnectionUpdateMessage("connected");
                i = i-n;
                progressive++;
            }
            nConn = MAXGIOC - checkClientAlive();
            log.addLog("Number of client(s) connected:" + nConn);
            tok.setInitNumberOfPlayers(nConn);
            for (int i = 0; i <nConn ; i++)
            {
                Thread t = new Thread(player.get(i));
                t.start();
                threadPlayers.add(t);
                log.addLog("Thread ServerPlayer " + i + " Started");
            }
        }
        catch (Exception e) {
            log.addLog("",e.getStackTrace());
            closeAllConnection();
            e.printStackTrace();
        }
    }


    //<editor-fold desc = "Window and objects initialization">
    /**
     *  Initialization board game for each players
     */
    private boolean initializeWindowPlayers()
    {
        int c1,c2;
        ArrayList<String []> cards;

        //Take all xml names of windows cards
        try{
            cards = ParserXML.readWindowsName("resources/vetrate/xml/windows_list.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            log.addLog("" , ex.getStackTrace());
            return false;
        }

        for (int i = 0; i < nConn ; i++)
        {
            //Pick up 2 random cards
            if (cards.size() > 2) {
                c1 = (int) (Math.random() * (cards.size()));
                do {
                    c2 = (int) (Math.random() * (cards.size()));
                } while (c1 == c2);
            } else {
                c1 = 0;
                c2 = 1;
            }

            player.get(i).setWindowCards(cards.get(c1), cards.get(c2));

            //Remove picked cards from list
            if (c1 > c2) {
                cards.remove(c1);
                cards.remove(c2);
            } else {
                cards.remove(c2);
                cards.remove(c1);
            }
        }
        return true;
    }

    /**
     *  Initialization private objective card for each players
     */
    private boolean initalizePrivateObjectiveCards ()
    {
        int c;
        ArrayList<String> cards;

        //Take all xml names of private objective cards
        try{
            cards = ParserXML.readObjectiveNames("resources/carte/obbiettivi/obbiettiviPrivati/xml/privateObjectiveList.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        try
        {
            //For each players initialize his own private objective randomly
            for (int i=0;i<nConn;i++)
            {
                c = (int)(Math.random()* (cards.size()));
                player.get(i).setPrivateObjCard(cards.get(c));
                cards.remove(c);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     *  Initialization public objective cards for each players
     */
    private boolean initializePublicObjectiveCards ()
    {
        int c;
        ArrayList<String> cards;

        //Take all xml names of public objective cards
        try{
            cards = ParserXML.readObjectiveNames("resources/carte/obbiettivi/obbiettiviPubblici/xml/publicObjectiveList.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        try
        {
            String [] objs = new String[3];
            //Select randomly 3 public objective cards from list
            for (int i = 0; i < 3; i++)
            {
                c = (int)(Math.random()* (cards.size()));
                objs[i]=cards.get(c);
                cards.remove(c);
            }
            //For each players initialize public objective already selected
            for (int i=0;i<nConn;i++)
                player.get(i).setPublicObjCard(objs);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    private void updateClient ()
    {
        for (int i = 0; i < player.size() ; i++)
            player.get(i).updateClient();

    }
    /**
     * To modify in reading from XML or DB possible users
     * @return
     */
    private ArrayList initializePossibleUsers ()
    {
        ArrayList<String> arr = new ArrayList();
        arr.add("A");
        arr.add("B");
        arr.add("C");
        arr.add("D");
        return arr;
    }



    /**
     * DA MODIFICARE!! non funziona con interface runnable, serve memorizzare i thread di serverPlayer
     */
    private void closeAllConnection ()
    {
        for (int i = 0; i < threadPlayers.size() ; i++)
            threadPlayers.get(i).interrupt();
        for (int i = 0; i < player.size() ; i++)
            player.get(i).closeConnection("Fatal Error");

        log.addLog("All connections are forced to stop cause fatal error");
    }

    /**
     * check if all clients are already connected
     * @return number of client disconnected
     */
    private int checkClientAlive ()
    {
        int nDisc = 0;
        try
        {
            for (int i = 0; i < player.size() ; i++)
            {
                if (!player.get(i).isClientAlive())
                {
                    nDisc ++ ;
                    player.remove(i);
                    log.addLog("Client does not respond to ping\r\n\t Client disconnected");
                }
            }
        }
        catch (Exception e)
        {
            log.addLog("" , e.getStackTrace());
            closeAllConnection();
        }
        return nDisc;
    }

    /**
     * Start client's setup comunication (Windows and it.polimi.ingsw.model.objectives)
     */
    private synchronized void waitInitialition ()
    {
        //Signal to start setup phase
        tok.startSetup();

        synchronized (tok)
        {
            try
            {
                log.addLog("Setup Phase started");
                //Wake Up all ServerPlayers to start setup phase
                tok.notifyAll();

                //Wait until end setup phase
                while (tok.getOnSetup())
                    tok.wait();
                log.addLog("Setup Phase ended");

            }
            catch (InterruptedException ex) {
                log.addLog("" , ex.getStackTrace());
                Thread.currentThread().interrupt();
                closeAllConnection();
            }
        }
    }

    private void clientConnectionUpdateMessage (String str)
    {
        for (int i = 0; i < player.size(); i++)
            player.get(i).sendMessage("Number of client " + str + ": "+ player.size());
    }
    //</editor-fold>

    public static void main(String[] args)
    {
        (new Thread(new MatchHandler())).start();
    }
}




