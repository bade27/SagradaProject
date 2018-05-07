package Server;

import Exceptions.ParserXMLException;
import Model.Dadiera;
import Utilities.ParserXML;
import Utilities.LogFile;

import java.util.ArrayList;

/**
 * Cose da aggiungere:
 *  -la funzione run() che gestisce la partita
 *  -l'oggetto comunicator che comunica col client da passare al serverPlayer
 *  -completare l'initiailizePlayers
 *  -completare la startGame
 */
public class MatchHandler implements Runnable
{
    private ArrayList<ServerPlayer> player;

    private int nConn;
    private TokenTurn tok;
    private Dadiera dices;

    private final static int MAXGIOC =2;//Da modificare a 4

    public synchronized void run ()
    {
        LogFile.cleanFile();

        acceptConnection();
        initiliazeWindowPlayers();
        waitInitialition();
        System.out.println(">>>Initialization ended");
        startGame();
    }

    private void startGame ()
    {
        LogFile.addLog("Game Phase started");

        dices.mix(tok.getNumPlayers());
        LogFile.addLog("Dadiera Mixed");
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
                    LogFile.addLog("Dadiera Mixed");
                }
                tok.nextTurn();
                tok.notifyAll();

                try {
                    tok.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    closeAllConnection();
                }
            }
        }
    }

    /**
     * Iniziatalize connecition with server for each players
     */
    private void acceptConnection()
    {
        System.out.println(">>>Server Started");
        LogFile.addLog("Server Started");
        nConn = 0;
        player = new ArrayList<ServerPlayer>();
        try
        {
            tok = new TokenTurn();
            dices = new Dadiera();
            ArrayList possibleUsrs = initializePossibleUsers();
            //Per ogni giocatore initzializza la comunicazione attendendo che qualche client si connetta
            for (int i =0 ;i< MAXGIOC;i ++)
            {
                ServerPlayer pl = new ServerPlayer(tok,new ServerModelAdapter(dices),possibleUsrs);
                if (pl.initializeComunication())
                {
                    player.add(pl);
                    nConn++;
                    LogFile.addLog("Client accepted");
                }
                else
                    i--;
                //n = CheckAlive();
                //if (n > 0)
                //  i = i-n;
            }
            LogFile.addLog("Number of Client connected:" + nConn);
            tok.setInitNumberOfPlayers(nConn);
            for (int i = 0; i <nConn ; i++)
            {
                Thread t = new Thread(player.get(i));
                t.start();
                LogFile.addLog("Thread ServerPlayer " + i + " Started");
            }
        }
        catch (Exception e) {
            LogFile.addLog("",e.getStackTrace());
            closeAllConnection();
            e.printStackTrace();
        }
    }

    /**
     *  Initialization board game for each players
     */
    private boolean initiliazeWindowPlayers ()
    {
        int c1,c2;
        ArrayList<String []> cards;

        //Take all xml names of windows cards
        try{
            cards = ParserXML.readWindowsName("resources/vetrate/xml/windows_list.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            LogFile.addLog("" , ex.getStackTrace());
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
     * Start client's setup comunication (Windows and Objectives)
     */
    private synchronized void waitInitialition ()
    {
        //Signal to start setup phase
        tok.startSetup();

        synchronized (tok)
        {
            try
            {
                LogFile.addLog("Setup Phase started");
                //Wake Up all ServerPlayers to start setup phase
                tok.notifyAll();

                //Wait until end setup phase
                while (tok.getOnSetup())
                        tok.wait();
                LogFile.addLog("Setup Phase ended");
            }
            catch (InterruptedException ex) {
                LogFile.addLog("" , ex.getStackTrace());
                closeAllConnection();
            }
        }
    }

    private void closeAllConnection ()
    {
        for (int i = 0; i < player.size() ; i++)
        {
            player.get(i).interrupt();
            player.get(i).closeComunication();
        }
        LogFile.addLog("All connections are forced to stop");
    }


    public static void main(String[] args)
    {
        (new Thread(new MatchHandler())).start();

    }
}




