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
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.UsersEntry;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MatchHandler implements Runnable
{
    //Match Objects
    private ArrayList<ServerPlayer> player;
    private ArrayList<Thread> threadPlayers;
    private int id;
    public LogFile log;

    //Game objects
    private TokenTurn token;
    private Dadiera dices;
    private RoundTrace roundTrace;
    private UsersEntry userList;
    private MainServerApplication mainServer;

    //game parameters
    private static int TURNS;
    private static int MAXGIOC;
    private static int TRESHGIOC;
    private static int TRESHTIME;
    private int nConn;

    //initial timer parameters
    private final Object lockOnnConn = new Object();
    private final Object gameCannotStartYet = new Object();
    private Thread timer;

    //disconnections parameters
    private final Object disconnCounterLock = new Object();
    private int disconnCounter = 0;
    private Thread reconnection;
    private final Object reconnLock = new Object();
    private final Object startGameLock = new Object();
    private boolean gameStarted = false;


    public MatchHandler (MainServerApplication main,UsersEntry users,int id)
    {
        this.id = id;
        mainServer = main;
        userList = users;
        log = new LogFile();
        log.createLogFile("Match_Handler" + id);
    }


    public synchronized void run ()
    {
        //Connection Phase
        if (!initializeServer())
        {
            log.addLog(">>>Failed to initialize Match Handler, Match Handler aborted");
            System.out.println(">>>Failed to initialize Match Handler" + id + ", Match Handler aborted");
            return;
        }


        //When thread that accept client's connection has the right number of clients, all clients connected will be initialized
        try{
            synchronized (gameCannotStartYet){
                while (nConn < TRESHGIOC)
                    gameCannotStartYet.wait();
            }
        }catch (Exception e){
            log.addLog("Fatal Error: Impossible to put in wait Server");
            Thread.currentThread().interrupt();
        }

        initializeClients();
        log.addLog(">>>Match Handler" + id + ": Connection Phase Ended");

        //Windows, tools and objectives initialization
        if (! (initializeWindowPlayers() && initializePublicObjectiveCards() && initalizePrivateObjectiveCards() && initializeTools()))
        {
            log.addLog(">>>Failed to initialize cards, server aborted");
            System.out.println(">>>Match Handler" + id + ": Failed to initialize cards, server aborted");
            return;
        }

        //Setup Phase
        waitInitialition();

        //removes players that didn't log in
        for(int i = 0; i < player.size(); i++)
            if(player.get(i).getUser() == null)
                player.remove(i);

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
        log.addLog("Game Phase started");
        setGameStarted(true);
        token.setOnGame(true);
        try{
            mixDadiera();
        }catch (Exception e){
            endGame();
            return;
        }

        turnsPlayed = 0;

        //updateClient();
        while (true)
        {
            synchronized (token.getSynchronator())
            {
                //If a fatal error happens close all connection and return
                if (token.isFatalError())
                    closeAllConnection();

                //If number of players remaining are not enough the game will stop
                if (token.getNumPlayers() <= 1)
                {
                    endGame();
                    return;
                }

                //On end round situation
                if (token.isEndRound())
                {
                    //Check if turns played are enough and close Match Handling
                    if (turnsPlayed == TURNS - 1)
                    {
                        endGame();
                        return;
                    }

                    //Update Round Trace
                    while(dices.getDiceList().size() > 0)
                    {
                        Dice tmp = null;
                        try {
                            tmp = dices.getDice(0);
                        } catch (IllegalDiceException e) {
                            e.printStackTrace();
                        }
                        roundTrace.addDice(turnsPlayed + 1, tmp);
                        dices.deleteDice(tmp);
                    }

                    //Increment total of turn
                    turnsPlayed++;
                    log.addLog("turn " + turnsPlayed);

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
                    log.addLog("Interrupted exception occurred", ex.getStackTrace());
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
            log.addLog("Dadiera Mixed");
        } catch (NotEnoughDiceException e) {
            log.addLog("No more dice into dice bag");
            throw new IllegalDiceException();
        }
    }

    private void endGame ()
    {
        log.addLog("The game is ended, count of players' point");

        //Conteggio dei punti da parte degli obbiettivi
        String[] users = new String[MAXGIOC];
        int[] pointsList = new int[MAXGIOC];

        for (int i = 0 ; i < player.size() ; i++)
        {
            if (player.get(i).isInitialized())
            {
                users[i] = player.get(i).getUser();
                if (player.get(i).isInGame())
                    pointsList[i] = player.get(i).getPoints();
                else
                    pointsList[i] = -1;
            }
        }

        for (int i = 0 ; i < player.size() ; i++)
            player.get(i).endGameCommunication(users,pointsList);

        for (int i = 0 ; i < player.size() ; i++)
            userList.setUserGameStatus(player.get(i).getUser(),false);

        token.setEndGame();
        token.getSynchronator().notifyAll();

        mainServer.removeMatchHandler(this);
        log.addLog("The game is ended, MatchHandler closing");
        System.out.println(">>>Match Handler" + id + ": The game is ended");
    }
    //</editor-fold>

    //<editor-fold desc="Connection Phase">

    /**
     * Initialize all server's components and all game's componenents
     */
    private boolean initializeServer ()
    {
        try {
            parametersSetup();
        } catch (ParserXMLException e) {
            log.addLog("Impossible to read settings parameters" , e.getStackTrace());
            return false;
        }

        timer = new Thread(new ConnectionTimer());
        timer.start();
        //LogFile.createLogFile();
        player = new ArrayList<ServerPlayer>();
        token = new TokenTurn();
        dices = new Dadiera();
        roundTrace = new RoundTrace();

        //nConn = 0;
        setnConn(0);


        System.out.println(">>>Match Handler" + id + " started");
        log.addLog("Match Handler started");

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
            subFromnConn(checkClientAlive());
            int n = getnConn();
            log.addLog("Number of client(s) connected:" + n);
            token.setInitNumberOfPlayers(n);
            //Starting of all ServerPlayer
            for (int i = 0; i < n ; i++)
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
    public boolean clientRegistration (ClientRemoteInterface cli)
    {
        //preventive check of the in-game players
        for(int i = 0; i < player.size(); i++) {
            if(!player.get(i).isInTurn()) {
                if(!player.get(i).isClientAlive()) {
                    if(player.get(i).getCommunicator() != null)
                        player.get(i).setPlayerAsOffline();
                }
            }
        }

        //waits for all the parameters to be correctly set
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            return false;
        }

        //If max number of connection is reached communicate the client he is one too many
        if (getnConn() == MAXGIOC || (isGameStarted() && getDisconnCounter() == 0))
        {
            log.addLog("Client Rejected cause too many client connected\n");
            log.addLog("getnConn() == MAXGIOC: " + (getnConn() == MAXGIOC)
                    + "\n(isGameStarted() && getDisconnCounter() == 0): "
                    + (isGameStarted() && getDisconnCounter() == 0) + "\n");
            return false;
        }
        if(getDisconnCounter() == 0) {
            //Initialization of ServerPlayer for each player
            ServerModelAdapter adp = new ServerModelAdapter(dices, roundTrace, token);
            try {
                cli.setAdapter(adp);
                cli.setMatchHandler(this);
            } catch (RemoteException e) {
                return false;
            }
            ServerPlayer pl = new ServerPlayer(token, adp, userList, cli, this);

            player.add(pl);
            addTonConn(1);
            int n = checkClientAlive();
            clientConnectionUpdateMessage("connessi");
            subFromnConn(n);
            //If max number of connection is reached starts game
            if (getnConn() == MAXGIOC) {
                synchronized (gameCannotStartYet) {
                    gameCannotStartYet.notifyAll();
                }
                timer.interrupt();
            }

            //If 2 connections reached starts the timer
            if (getnConn() == TRESHGIOC) {
                synchronized (lockOnnConn) {
                    lockOnnConn.notifyAll();
                }
            }
        } else {
            //If is some clients want to reconnect this thread handles the procedure
            String u = "";
            try {
                u = cli.getName();
                cli.setMatchHandler(this);
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println(u + "not accepted");
                return false;
            }
            ServerPlayer newSP = null;
            //search for the server player corresponding to the client that wants to reconnect
            for (int i = 0; i < player.size(); i++)
                if (!player.get(i).isInGame() && player.get(i).getUser().equals(u))
                    newSP = player.get(i);
            if (newSP != null) {
                //set the new communicator
                newSP.setCommunicator(cli);
                try {
                    //set the old adapter on the old communicator
                    newSP.getCommunicator().setAdapter(newSP.getAdapter());
                } catch (RemoteException e) {
                    log.addLog("Impossible to set the adapter on the new Communicator\n",e.getStackTrace());
                    System.out.println(u + "not accepted");

                    return false;
                }
                newSP.setInGame(true);
                decDisconnCounter();
                newSP.reconnected();
            } else {
                System.out.println(u + " not accepted");
                return false;
            }
            System.out.println(u + " accepted");
            addTonConn(1);
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="nConn management">
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
    //</editor-fold>

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
                log.addLog("Setup Phase started");
                //Wake Up all ServerPlayers to start setup phase
                token.getSynchronator().notifyAll();

                //Wait until end setup phase
                while (token.getOnSetup())
                    token.getSynchronator().wait();
                log.addLog("Setup Phase ended");

            }
            catch (InterruptedException ex) {
                log.addLog("" , ex.getStackTrace());
                Thread.currentThread().interrupt();
                closeAllConnection();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc = "Windows, tools and objectives initialization">

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
            log.addLog("Impossible to read XML Window",ex.getStackTrace());
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
            log.addLog("Impossible to read XML Private Obj",ex.getStackTrace());
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
            log.addLog("Impossible to set XML private Obj",ex.getStackTrace());
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
            log.addLog("Impossible to read XML publicObj",ex.getStackTrace());
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
            log.addLog("Impossible to set XML publicObj",ex.getStackTrace());
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
            log.addLog("Impossible to read XML tools",ex.getStackTrace());
            return false;
        }

        ArrayList<Integer> toolNum = new ArrayList<>();
        do {
            int i = new Random().nextInt(12);
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

            //Used to test tools, do not delete
            /*tools[0] = ToolsFactory.getTools(toolNames[0]);
            tools[1] = ToolsFactory.getTools(toolNames[1]);
            tools[2] = ToolsFactory.getTools(toolNames[3]);*/

            //For each players initialize tool cards already selected
            int n = getnConn();
            for (int i=0;i< n/*nConn*/;i++)
                player.get(i).setToolCards(tools);
        }
        catch (Exception ex) {
            log.addLog("Impossible to set XML tools",ex.getStackTrace());
            return false;
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">

    public int getId (){return id;}
    /**
     * Update all client's graphic
     */
    public void updateClient ()
    {
        ArrayList<String> users = new ArrayList<>();
        ArrayList<Pair[][]> pairs = new ArrayList<>();
        ArrayList<Boolean> actives = new ArrayList<>();
        for (int j = 0; j < player.size(); j++)
        {
            users.add(player.get(j).getUser());
            pairs.add( player.get(j).getGrid());
            actives.add(player.get(j).isInGame());
        }

        for (int i = 0; i < player.size(); i++)
        {
            //Update user's window,dadiera,round trace and markers
            if (player.get(i).isInGame())
            {
                if (!player.get(i).updateClient())
                {
                    for (int j = 0 ; j < users.size() ; j++)
                    {
                        if (users.get(j).equals(player.get(i).getUser()))
                            actives.set(j,false);
                    }
                }
            }
        }

        for (int i = 0; i < player.size(); i++)
        {
            try {
                //Update others users with user's window,dadiera,roundttrace and markers
                if(player.get(i).isInGame())
                    player.get(i).updateOpponents(users,pairs,actives);
            } catch (ClientOutOfReachException e) {
                log.addLog("Client  temporarily unreachable");
            }
        }

    }

    /**
     * sets up parameters
     */
    private static void parametersSetup() throws ParserXMLException
    {
        TURNS = ParserXML.SetupParserXML.getTotalTurns(FileLocator.getGameSettingsPath());
        MAXGIOC = ParserXML.SetupParserXML.getMaxPlayers(FileLocator.getGameSettingsPath());
        TRESHGIOC = ParserXML.SetupParserXML.getThresholdPlayers(FileLocator.getGameSettingsPath());
        TRESHTIME = ParserXML.SetupParserXML.getThresholdTimeLaps(FileLocator.getGameSettingsPath());
    }


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
                    //player.remove(i);
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
     * Send to client a message about connection of other clients
     * @param str Type of message to send
     */
    private void clientConnectionUpdateMessage (String str)
    {
        for (int i = 0; i < player.size(); i++)
            player.get(i).sendMessage("Numero di client " + str + ": "+ player.size());
    }
    //</editor-fold>

    //<editor-fold desc="Initial timer">
    /**
     * This is the timer that starts when the number of connected clients
     * is two, and is stopped if the number of connected players reaches MAXGIOC
     */
    private class ConnectionTimer implements Runnable {
        @Override
        public void run() {
            synchronized (lockOnnConn) {
                while (nConn < TRESHGIOC) {
                    try {
                        lockOnnConn.wait();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        return;
                    }
                }
            }

            try {
                TimeUnit.SECONDS.sleep(TRESHTIME);
            } catch (InterruptedException e) {
                return;
            }

            synchronized (gameCannotStartYet) {
                gameCannotStartYet.notifyAll();
            }

            token.stopSetup();

        }
    }
    //</editor-fold>

    //<editor-fold desc="Reconnection methods">

    public int getDisconnCounter() {
        synchronized (disconnCounterLock) {
            return disconnCounter;
        }
    }

    public void incDisconnCounter() {
        synchronized (disconnCounterLock) {
            this.disconnCounter++;
        }
    }

    public void decDisconnCounter() {
        synchronized (disconnCounterLock) {
            this.disconnCounter--;
        }
    }

    //</editor-fold>


    public boolean isGameStarted() {
        synchronized (startGameLock) {
            return gameStarted;
        }
    }

    public void setGameStarted(boolean gameStarted) {
        synchronized (startGameLock) {
            this.gameStarted = gameStarted;
        }
    }
}




