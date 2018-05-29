package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class MatchHandler implements Runnable
{
    private LogFile log;

    private ArrayList<ServerPlayer> player;
    private ArrayList<Thread> threadPlayers;
    private ArrayList possibleUsrs;

    private int nConn;
    private TokenTurn tok;
    private Dadiera dices;

    private final static int MAXGIOC = 2;//Da modificare a 4

    //connection parameters
    private static final String settings = "resources/server_settings.xml";
    private static int RMI_REGISTRY_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private int progressive;

    /**
     * sets up connection parameters
     */
    private static void connection_parameters_setup() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(settings);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        //rmi setup
        RMI_REGISTRY_PORT = Integer.parseInt(document.getElementsByTagName("registryPort").item(0).getTextContent());
        HOSTNAME = document.getElementsByTagName("hostName").item(0).getTextContent();

        //socket setup
        SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
    }


    public synchronized void run ()
    {
        log = new LogFile("ServerLog");
        possibleUsrs = initializePossibleUsers();
        player = new ArrayList<ServerPlayer>();
        tok = new TokenTurn();
        dices = new Dadiera();
        progressive = 0;
        try {
            connection_parameters_setup();
        } catch (ParserConfigurationException| IOException | SAXException e) {
            log.addLog("Impossible to read settings parameters" , e.getStackTrace());
        }
        nConn = 0;


        System.out.println(">>>Server started");
        log.addLog(">>>Server started");

        InitializerConnection initializer = new InitializerConnection(this);
        initializer.start();

        try{
            synchronized (tok){
                tok.wait();
            }
        }catch (Exception e){
            log.addLog("Impossible to put in wait Server");
        }


        System.out.println(">>>Connection Ended");

        initializeServer();
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
        dices.mix(tok.getNumPlayers());
        log.addLog("Dadiera Mixed");

        boolean b = true;

        while (b)
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

    //<editor-fold desc="Initialization Phase">
    /**
     * Initialize connection with server for each players
     */
    private void initializeServer()
    {
        threadPlayers = new ArrayList<Thread>();
        try
        {
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

    /**
     * Registration of client's communicator object passed and starting of relative thread
     * @param cli communicator object, used for interact with client
     */
    private void clientRegistration (ClientRemoteInterface cli)
    {
        //If max number of connection is reached communicate the client he is one too many
        if (nConn == MAXGIOC)
        {
            try {
                cli.sendMessage("Too many client connected");
                return;
            }catch (ClientOutOfReachException | RemoteException e){
                log.addLog("Impossible to communicate the client he is one too many");
            }

        }
        ServerModelAdapter adp = new ServerModelAdapter(dices);
        try{
            cli.setModelAdapter(adp);
        }catch (RemoteException e){

        }

        ServerPlayer pl = new ServerPlayer(tok,adp,possibleUsrs,log,cli);
        player.add(pl);
        nConn++;
        int n = checkClientAlive();
        clientConnectionUpdateMessage("connected");
        nConn = nConn-n;

        try
        {
            //If max number of connection is reached starts game
            if (nConn == MAXGIOC)
            {
                synchronized (tok){
                    tok.notifyAll();
                }
            }
        }catch ( Exception e){
            e.printStackTrace();
            log.addLog("Impossible to notify server handler");
        }

    }

    /**
     * Update RMI's registry after an RMI's connection and call clientRegistration
     * @param cli communicator object, used for interact with client
     */
    public void setClient (ClientRemoteInterface cli)
    {
        ServerRmiHandler rmiCon;
        progressive++;
        log.addLog("Client accepted with RMI connection");
        //RMI Registry creation and bind server name
        try {
            rmiCon = new ServerRmiHandler(this);

            String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
            try{
                java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            }catch (Exception ex){}

            Naming.bind(bindLocation, rmiCon );

            log.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
        }catch (Exception e) {
            log.addLog("RMI Bind failed" , e.getStackTrace());
        }
        clientRegistration(cli);
    }
    //</editor-fold>

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

    /**
     * Update all client's graphic
     */
    private void updateClient ()
    {
        for (int i = 0; i < player.size() ; i++)
        {
            if(player.get(i).updateClient()) {
                for (int j = 0; j < player.size(); j++) {
                    if (player.get(j).getUser() != player.get(i).getUser()) {

                        try {
                            player.get(j).updateOpponents(player.get(i).getUser(), player.get(i).getGrid());
                        } catch (ClientOutOfReachException e) {
                            log.addLog("Client " + player.get(j).getUser() + " temporarily unreachable");
                        }

                    }
                }
            }
        }

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

    //<editor-fold desc="Initializer connection class">
    /**
     * This thread-class is used to accept client's connection through RMI and Socket in a parallel process
     */
    private class InitializerConnection extends Thread
    {
        MatchHandler match;
        private InitializerConnection (MatchHandler m)
        {
            match = m;
        }

        public void run ()
        {
            ServerRmiHandler rmiCon;
            //RMI Registry creation and bind server name
            try {
                rmiCon = new ServerRmiHandler(match);

                String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
                try{
                    java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
                }catch (Exception ex){}

                Naming.bind(bindLocation, rmiCon );

                log.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
            }catch (Exception e) {
                log.addLog("RMI Bind failed" , e.getStackTrace());
            }

            //Socket creation and accept
            while (true)
            {
                ServerSocketHandler socketCon;
                try{
                    socketCon = new ServerSocketHandler(log, SOCKET_PORT);
                    socketCon.createConnection();
                    if (socketCon.isConnected()) {
                        log.addLog("Client accepted with Socket connection");
                    }
                    match.clientRegistration(socketCon);
                }
                catch (ClientOutOfReachException e) {
                    log.addLog(e.getMessage() , e.getStackTrace());
                }
            }
        }
    }
    //</editor-fold>
}




