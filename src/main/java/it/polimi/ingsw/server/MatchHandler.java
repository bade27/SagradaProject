package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.NotEnoughDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.model.tools.ToolsFactory;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.UsersEntry;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MatchHandler implements Runnable
{
    private ArrayList<ServerPlayer> player;
    private ArrayList<Thread> threadPlayers;

    private TokenTurn token;
    private Dadiera dices;
    private RoundTrace roundTrace;
    private UsersEntry userList;

    private final static int TURNS = 3;
    private final static int MAXGIOC = 1;//Da modificare a 4

    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;

    private int progressive;
    private int nConn;

    private final Object lockOnnConn = new Object();
    private final Object gameCannotStartYet = new Object();
    private Thread timer;
    private final int threshold = 2;
    private final int sleepTime = 10;

    private class ConnectionTimer implements Runnable {
        @Override
        public void run() {
            synchronized (lockOnnConn) {
                while (nConn < threshold) {
                    try {
                        lockOnnConn.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("dormo");
            try {
                TimeUnit.SECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                return;
            }

            System.out.println("mi sveglio");
            synchronized (gameCannotStartYet) {
                gameCannotStartYet.notifyAll();
            }

            token.stopSetup();

        }
    }


    public synchronized void run ()
    {
        //Connection Phase
        if (!initializeServer())
        {
            LogFile.addLog(">>>Failed to initialize server, server aborted");
            System.out.println(">>>Failed to initialize server, server aborted");
            return;
        }


        //When thread that accept client's connection has the right number of clients, all clients connected will be initialized
        try{
            synchronized (gameCannotStartYet){
                while (nConn < threshold)
                    gameCannotStartYet.wait();
            }
        }catch (Exception e){
            LogFile.addLog("Fatal Error: Impossible to put in wait Server");
            Thread.currentThread().interrupt();
        }

        initializeClients();
        System.out.println(">>>Connection Phase Ended");

        //Windows, tools and objectives initialization
        if (! (initializeWindowPlayers() && initializePublicObjectiveCards() && initalizePrivateObjectiveCards() && initializeTools()))
        {
            LogFile.addLog(">>>Failed to initialize cards, server aborted");
            System.out.println(">>>Failed to initialize cards, server aborted");
            return;
        }

        //Setup Phase
        waitInitialition();
        System.out.println(">>>Setup Phase ended");

        //Game Phase
        startGame();
    }

    //<editor-fold desc="Game Phase">
    /**
     * Manage the match: checks status, wakes and updates players, and manages match components
     * */
    private void startGame ()
    {
        int turnsPlayed;
        LogFile.addLog("Game Phase started");
        try{
            mixDadiera();
        }catch (Exception e){
            endGame();
            return;
        }

        turnsPlayed = 0;

        while (true)
        {
            synchronized (token.getSynchronator())
            {
                //If a fatal error happens close all connection and return
                if (token.isFatalError())
                    closeAllConnection();

                //If number of players remaining are not enough the game will stop
                /*if (token.getNumPlayers() <= 1)
                {
                    endGame();
                    return;
                }*/

                //On end round situation
                if (token.isEndRound())
                {
                    //Check if turns played are enough and close Match Handling
                    if (turnsPlayed == TURNS - 1)
                    {
                        endGame();
                        return;
                    }

                    //Increment total of turn
                    turnsPlayed++;

                    //Update Round Trace
                    while(dices.getDiceList().size() > 0)
                    {
                        Dice tmp = null;
                        try {
                            tmp = dices.getDice(0);
                        } catch (IllegalDiceException e) {
                            e.printStackTrace();
                        }
                        roundTrace.addDice(turnsPlayed, tmp);
                        dices.deleteDice(tmp);
                    }

                    //and..Mix dadiera
                    try{
                        mixDadiera();
                    }catch (Exception e){
                        endGame();
                        return;
                    }
                }

                //Update client's graphic situation
                updateClient();

                //Finish turn and notify all clients
                token.nextTurn();
                token.getSynchronator().notifyAll();

                try {
                    token.getSynchronator().wait();
                }
                catch (InterruptedException ex)
                {
                    LogFile.addLog("Interrupted exception occurred", ex.getStackTrace());
                    ex.printStackTrace();
                    Thread.currentThread().interrupt();
                    closeAllConnection();
                }
            }
        }
    }

    /**
     *  Pick up new dice from dice bag
     */
    private void mixDadiera () throws IllegalDiceException
    {
        try {
            dices.mix(token.getNumPlayers());
            System.out.println(">>>Dadiera Mixed");
            LogFile.addLog("Dadiera Mixed");
        } catch (NotEnoughDiceException e) {
            LogFile.addLog("No more dice into dice bag");
            throw new IllegalDiceException();
        }
    }

    private void endGame ()
    {
        System.out.println(">>>The game is ended");
        LogFile.addLog("The game is ended, count of players' point");

        //Conteggio dei punti da parte degli obbiettivi
        String[] userList = new String[MAXGIOC];
        int[] pointsList = new int[MAXGIOC];

        for (int i = 0 ; i < player.size() ; i++)
        {
            if (player.get(i).isInitialized())
            {
                userList[i] = player.get(i).getUser();
                pointsList[i] = player.get(i).getPoints();
            }
        }

        for (int i = 0 ; i < player.size() ; i++)
            player.get(i).endGameCommunication(userList,pointsList);

        token.setEndGame();
        token.getSynchronator().notifyAll();

        LogFile.addLog("The game is ended, MatchHandler closing");
    }
    //</editor-fold>

    //<editor-fold desc="Connection Phase">

    /**
     * Initialize all server's components and all game's componenents
     */
    private boolean initializeServer ()
    {
        timer = new Thread(new ConnectionTimer());
        timer.start();
        LogFile.createLogFile();
        player = new ArrayList<ServerPlayer>();
        token = new TokenTurn();
        dices = new Dadiera();
        roundTrace = new RoundTrace();
        progressive = 0;
        if (!initializeUsers())
            return false;

        try {
            connection_parameters_setup();
        } catch (ParserConfigurationException| IOException | SAXException e) {
            LogFile.addLog("Impossible to read settings parameters" , e.getStackTrace());
            return false;
        }
        //nConn = 0;
        setnConn(0);


        System.out.println(">>>Server started");
        LogFile.addLog(">>>Server started");

        //Starts thread that accept client's connection
        InitializerConnection initializer = new InitializerConnection(this);
        initializer.start();

        return true;
    }


    /**
     * Initialize connection with server for each players after the connection of all of them
     */
    private void initializeClients()
    {
        threadPlayers = new ArrayList<Thread>();
        try
        {
            //Check connection status
            //nConn = MAXGIOC - checkClientAlive();
            subFromnConn(checkClientAlive());
            int n = getnConn();
            LogFile.addLog("Number of client(s) connected:" + n/*nConn*/);
            token.setInitNumberOfPlayers(n/*nConn*/);
            //Starting of all ServerPlayer
            for (int i = 0; i < n/*nConn*/ ; i++)
            {
                Thread t = new Thread(player.get(i));
                t.start();
                threadPlayers.add(t);
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
     * Registration of client's communicator object passed and starting of relative thread
     * @param cli communicator object, used for interact with client
     */
    private ServerModelAdapter clientRegistration (ClientRemoteInterface cli)
    {
        //If max number of connection is reached communicate the client he is one too many
        if (/*nConn*/getnConn() == MAXGIOC)
        {
            try {
                LogFile.addLog("Client Rejected cause too many client connected");
                cli.sendMessage("Too many client connected");
                return null;
            }catch (ClientOutOfReachException | RemoteException e){
                LogFile.addLog("Impossible to communicate the client he is one too many");
            }

        }
        //Initialization of ServerPlayer for each player
        ServerModelAdapter adp = new ServerModelAdapter(dices, roundTrace, token);
        ServerPlayer pl = new ServerPlayer(token,adp,userList,cli);
        player.add(pl);
        //nConn++;
        addTonConn(1);
        int n = checkClientAlive();
        clientConnectionUpdateMessage("connessi");
        //nConn = nConn-n;
        subFromnConn(n);

        //If max number of connection is reached starts game
        if (/*nConn*/getnConn() == MAXGIOC)
        {
            synchronized (gameCannotStartYet){
                gameCannotStartYet.notifyAll();
            }
            timer.interrupt();
        }

        if(getnConn() == threshold) {
            synchronized (lockOnnConn) {
                lockOnnConn.notifyAll();
            }
        }

        return adp;
    }

    /**
     * Update RMI's registry after an RMI's connection and call clientRegistration
     * @param cli communicator object, used for interact with client
     */
    public ServerModelAdapter setClient (ClientRemoteInterface cli)
    {
        ServerRmiHandler rmiCon;
        progressive++;
        LogFile.addLog("Client accepted with RMI connection");
        //RMI Registry creation and bind server name
        try {
            rmiCon = new ServerRmiHandler(this);

            String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
            try{
                java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            }catch (Exception ex){}

            Naming.bind(bindLocation, rmiCon );

            LogFile.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
        }catch (Exception e) {
            LogFile.addLog("RMI Bind failed" , e.getStackTrace());
        }
        return clientRegistration(cli);
    }
    //</editor-fold>

    public int getnConn() {
        synchronized (lockOnnConn) {
            return nConn;
        }
    }

    public void addTonConn(int n) {
        synchronized (lockOnnConn) {
            this.nConn += n;
        }
    }

    public void subFromnConn(int n) {
        synchronized (lockOnnConn) {
            this.nConn -= n;
        }
    }

    public void setnConn(int nConn) {
        synchronized (lockOnnConn) {
            this.nConn = nConn;
        }
    }

    //<editor-fold desc="Setup Phase">
    /**
     * Start client's setup comunication (Windows and it.polimi.ingsw.model.objectives)
     */
    private synchronized void waitInitialition ()
    {
        //Signal to start setup phase
        token.startSetup();

        synchronized (token.getSynchronator())
        {
            try
            {
                LogFile.addLog("Setup Phase started");
                //Wake Up all ServerPlayers to start setup phase
                token.getSynchronator().notifyAll();

                //Wait until end setup phase
                while (token.getOnSetup())
                    token.getSynchronator().wait();
                LogFile.addLog("Setup Phase ended");

            }
            catch (InterruptedException ex) {
                LogFile.addLog("" , ex.getStackTrace());
                Thread.currentThread().interrupt();
                closeAllConnection();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc = "Windows, tools and objectives initialization">

    /**
     * Initialize from file users's credential
     * @return true if goes well, false otherwise
     */
    private boolean initializeUsers ()
    {
        try
        {
            userList = new UsersEntry();
            return true;
        }catch (Exception e){
            LogFile.addLog(e.getMessage(),e.getStackTrace());
            return false;
        }
    }


    /**
     *  Initialization board game for each players
     */
    private boolean initializeWindowPlayers()
    {
        int c1,c2;
        ArrayList<String []> cards;

        //Take all xml names of windows cards
        try{
            cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
        }
        catch (ParserXMLException ex){
            LogFile.addLog("Impossible to read XML Window",ex.getStackTrace());
            return false;
        }

        int n = getnConn();
        for (int i = 0; i < n/*nConn*/ ; i++)
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
            cards = ParserXML.readObjectiveNames(FileLocator.getPrivateObjectivesListPath());
        }
        catch (ParserXMLException ex){
            LogFile.addLog("Impossible to read XML Private Obj",ex.getStackTrace());
            return false;
        }

        try
        {
            //For each players initialize his own private objective randomly
            int n = getnConn();
            for (int i=0;i<n/*nConn*/;i++)
            {
                c = (int)(Math.random()* (cards.size()));
                player.get(i).setPrivateObjCard(cards.get(c));
                cards.remove(c);
            }
        }
        catch (Exception ex) {
            LogFile.addLog("Impossible to set XML private Obj",ex.getStackTrace());
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
            cards = ParserXML.readObjectiveNames(FileLocator.getPublicObjectivesListPath());
        }
        catch (ParserXMLException ex){
            LogFile.addLog("Impossible to read XML publicObj",ex.getStackTrace());
            return false;
        }

        try
        {
            String [] objStrings = new String[3];
            //Select randomly 3 public objective cards from list
            for (int i = 0; i < 3; i++)
            {
                c = (int)(Math.random()* (cards.size()));
                objStrings[i]=cards.get(c);
                cards.remove(c);
            }

            //creation of the actual cards
            PublicObjective[] pubObjs = new PublicObjective[3];
            for(int i = 0; i < 3; i++) {
                pubObjs[i] = ObjectivesFactory.getPublicObjective(objStrings[i]);
            }

            //For each players initialize public objective already selected
            int n = getnConn();
            for (int i=0;i<n/*nConn*/;i++)
                player.get(i).setPublicObjCard(pubObjs);
        }
        catch (Exception ex) {
            LogFile.addLog("Impossible to set XML publicObj",ex.getStackTrace());
            return false;
        }
        return true;
    }

    /**
     *  Initialization tool cards for each players
     */
    private boolean initializeTools ()
    {
        int c;
        ArrayList<String> toolNamesTmp;
        String[] toolNames;

        //Take all xml names of tool cards
        try{
            toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
            toolNames = toolNamesTmp.toArray(new String[toolNamesTmp.size()]);
        }
        catch (ParserXMLException ex){
            LogFile.addLog("Impossible to read XML tools",ex.getStackTrace());
            return false;
        }

        ArrayList<Integer> toolNum = new ArrayList<>();
        do {
            int i = new Random().nextInt(6);
            if(!toolNum.contains(i))
                toolNum.add(i);
        } while(toolNum.size() < 3);

        try
        {
            Tools[] tools = new Tools[3];
            //Select and create randomly 3 tool cards from list of tools
            for (int i = 0; i < 3; i++)
            {
                c = toolNum.get(i);
                tools[i] = ToolsFactory.getTools(toolNames[c]);
            }

            //For each players initialize tool cards already selected
            int n = getnConn();
            for (int i=0;i< n/*nConn*/;i++)
                player.get(i).setToolCards(tools);
        }
        catch (Exception ex) {
            LogFile.addLog("Impossible to set XML tools",ex.getStackTrace());
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">

    /**
     * Update all client's graphic
     */
    public void updateClient ()
    {
        for (int i = 0; i < player.size(); i++)
        {
            //Update user's window,dadiera,roundttrace and markers
            if (player.get(i).isInGame())
            {
                if (player.get(i).updateClient()) {
                    for (int j = 0; j < player.size(); j++) {
                        if (!player.get(j).getUser().equals(player.get(i).getUser())) {

                            try {
                                //Update others users with user's window,dadiera,roundttrace and markers
                                player.get(j).updateOpponents(player.get(i).getUser(), player.get(i).getGrid());
                            } catch (ClientOutOfReachException e) {
                                LogFile.addLog("Client " + player.get(j).getUser() + " temporarily unreachable");
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * sets up connection parameters
     */
    private static void connection_parameters_setup() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(FileLocator.getServerSettingsPath());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        //rmi setup
        RMI_REGISTRY_PORT = Integer.parseInt(document.getElementsByTagName("registryPort").item(0).getTextContent());
        HOSTNAME = document.getElementsByTagName("hostName").item(0).getTextContent();

        //socket setup
        SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
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

        LogFile.addLog("All connections are forced to stop cause fatal error");
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
                    LogFile.addLog("Client does not respond to ping\r\n\t Client disconnected");
                }
            }
        }
        catch (Exception e)
        {
            LogFile.addLog("" , e.getStackTrace());
            closeAllConnection();
        }
        return nDisc;
    }

    /**
     * Send to client a message about connection of other clients
     * @param str Type of message to send
     */
    private void clientConnectionUpdateMessage (String str)
    {
        for (int i = 0; i < player.size(); i++)
            player.get(i).sendMessage("Numero di client " + str + ": "+ player.size());
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

                LogFile.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
            }catch (Exception e) {
                LogFile.addLog("RMI Bind failed" , e.getStackTrace());
            }

            //Socket creation and accept
            while (true)
            {
                try{
                    ServerSocketHandler socketCon = new ServerSocketHandler(SOCKET_PORT);
                    socketCon.createConnection();
                    if (socketCon.isConnected()) {
                        LogFile.addLog("Client accepted with Socket connection");
                    }
                    ServerModelAdapter adp = match.clientRegistration(socketCon);
                    socketCon.setMatch(match);
                    socketCon.setAdapter(adp);
                }
                catch (ClientOutOfReachException e) {
                    LogFile.addLog(e.getMessage() , e.getStackTrace());
                }
            }
        }
    }
    //</editor-fold>
}




