package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.UsersEntry;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

public class ServerPlayer implements Runnable {
    //game parameters
    private static int PING_TIMEOUT;
    private static int SETUP_TIMEOUT;
    private static int TURN_TIMEOUT;

    private ClientRemoteInterface communicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private MatchHandler mymatch;
    private String user;
    private boolean alive;

    //Setup Phase
    private UsersEntry possibleUsers;
    private boolean inGame;
    private boolean initialized;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Future<?> task = null;
    private Thread setupTimer;

    //passed parameters
    private String[] windowCard1, windowCard2;
    private String privateObjCard;

    //actual cards
    private Tools[] toolCards;
    private PublicObjective[] publicObjectives;

    //turn phase
    private final Object interruptionLock = new Object();
    private boolean turnInterrupted = false;

    public LogFile log;


    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, UsersEntry ps, ClientRemoteInterface cli, MatchHandler match)
    {
        adapter = adp;
        mymatch = match;
        log = mymatch.log;
        adapter.setServerPlayer(this);
        token = tok;
        possibleUsers = ps;
        communicator = null;
        alive = true;
        communicator = cli;
        inGame = true;
        initialized = false;
        try {
            parametersSetup();
        } catch (ParserXMLException e) {
            log.addLog("Impossible to read settings parameters", e.getStackTrace());
        }
    }

    public synchronized void run() {
        if (!initialized) {
            //////SETUP PHASE//////
            try {
                synchronized (token.getSynchronator()) {
                    //Wait until matchHandler signal start setup
                    while (!token.getOnSetup())
                        token.getSynchronator().wait();
                }

                //Initialization of client
                try {
                    setupTimer = new Thread(new SetUpTimer());
                    setupTimer.start();
                    login();
                    token.addPlayer(user);
                    initializeWindow();
                    initializeCards();
                    setupTimer.interrupt();
                } catch (ClientOutOfReachException | ModelException ex) {
                    log.addLog("(User: " + user + ") cannot complete setup" + "\n"
                            + ex.getStackTrace().toString());
                    //Notify token that client is dead
                    token.deletePlayer(user);
                    token.setJustDeleting(false);
                    //notify match that client disconnected
                    mymatch.incDisconnCounter();
                    mymatch.subFromnConn(1);
                    possibleUsers.setUserGameStatus(user, false);
                    closeConnection("Timeout Expired");
                    inGame = false;
                    token.endSetup();
                    synchronized (token.getSynchronator()) {
                        token.getSynchronator().notifyAll();
                    }
                    //If client fail initialization, he will not return on game
                    return;
                }

                //End Setup phase comunication
                initialized = true;
                token.endSetup();
                synchronized (token.getSynchronator()) {
                    token.getSynchronator().notifyAll();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                log.addLog("Fatal error on thread " + user, ex.getStackTrace());
                token.notifyFatalError();
                Thread.currentThread().interrupt();
            }
        }

        //////GAME PHASE//////
        while (inGame)//Da cambiare con la condizione di fine partita
        {
            turnInterrupted = false;
            synchronized (token.getSynchronator()) {
                try {
                    //Wait his turn
                    while (!token.isMyTurn(user))
                        token.getSynchronator().wait();

                    adapter.setTurnDone(false);

                    if (token.isEndGame()) {
                        log.addLog("(User:" + user + ")" + " End communication with client and close connection");
                        token.getSynchronator().notifyAll();
                        return;
                    }

                    log.addLog("Turn of:" + user);

                    try {
                        adapter.setCanMove(true);
                        clientTurn();
                    } catch (ClientOutOfReachException e) {
                        //closeConnection("Timeout Expired");
                        communicator = null;
                        log.addLog("(User: " + user + ")" + "player disconnected cause client unreachable");
                        token.deletePlayer(user);
                        possibleUsers.setUserGameStatus(user, false);
                        mymatch.incDisconnCounter();
                        mymatch.subFromnConn(1);
                        inGame = false;
                        token.getSynchronator().notifyAll();
                        break;
                    }

                    adapter.setTimer(TURN_TIMEOUT);
                    adapter.startTimer();

                    //End turn comunication
                    synchronized (adapter) {
                        adapter.wait();
                    }

                    if (isTurnInterrupted()) {
                        //closeConnection("Timeout Expired");
                        communicator = null;
                        log.addLog("(User: " + user + ")" + "player disconnected cause client late in response");
                        token.deletePlayer(user);
                        possibleUsers.setUserGameStatus(user, false);
                        mymatch.incDisconnCounter();
                        mymatch.subFromnConn(1);
                        inGame = false;
                        token.getSynchronator().notifyAll();
                        break;
                    } //else System.out.println("turn ended well");

                    token.getSynchronator().notifyAll();
                    token.getSynchronator().wait();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    log.addLog("Fatal error on thread " + user, ex.getStackTrace());
                    token.notifyFatalError();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    //<editor-fold desc="Setup Phase">

    /**
     * This is the timer for the connection phase
     */
    private class SetUpTimer implements Runnable {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(SETUP_TIMEOUT);
            } catch (InterruptedException e) {
                return;
            }

            //if time expires ongoing task (login or window choice) must be cancelled
            do {
                task.cancel(true);
            } while(!task.isCancelled());
        }
    }

    /**
     * Initialize username through login phase
     *
     * @throws ClientOutOfReachException client is out of reach
     */
    private void login() throws ClientOutOfReachException {
        String u = "";
        try {
            do {
                task = executor.submit(() -> communicator.login());
                u = task.get().toString();
            } while (!possibleUsers.loginCheck(u));
        } catch (InterruptedException|ExecutionException|CancellationException e) {
            log.addLog("Failed to add user");
            throw new ClientOutOfReachException();
        } catch (ParserXMLException e) {
            log.addLog("Unable to read list of players");
            throw new ClientOutOfReachException();
        }
        user = u;
        adapter.setUser(u);
        log.addLog("User: " + user + " Added");
    }

    /**
     * Initialize client's window board
     *
     * @throws ClientOutOfReachException client is out of reach
     * @throws ModelException            Impossible to set window
     */
    private void initializeWindow() throws ClientOutOfReachException, ModelException {
        String s1;
        task = executor.submit(() -> communicator.chooseWindow(windowCard1, windowCard2));
        try {
            s1 = task.get().toString();
        } catch (InterruptedException|ExecutionException|CancellationException e) {
            s1 = windowCard1[0];
            initialized = true;
            log.addLog("(User:" + user + ") unable to choose the window.\n" +
                    " The game chooses for him this window: " + s1);
            windowCreation(s1);
            throw new ClientOutOfReachException();
        }

        windowCreation(s1);
    }

    /**
     * this class performs the actual creation of the window
     * @param s1 window path
     * @throws ModelException
     */
    private void windowCreation(String s1) throws ModelException {
        try {
            adapter.initializeWindow(s1);
            log.addLog("User: " + user + " Window initialized ");
        } catch (ModelException ex) {
            log.addLog("Impossible to set Window from XML", ex.getStackTrace());
            throw new ModelException();
        }
    }

    /**
     * receives the public objectives and the tools chosen for the current match
     * and sets the corresponding parameters on the model adapter
     */
    private void initializeCards() throws ClientOutOfReachException, ModelException {
        try {
            boolean performed;
            String[] toolnames = Arrays.stream(toolCards)
                    .map(t -> t.getName()).toArray(String[]::new);
            String[] publicObjNames = Arrays.stream(publicObjectives)
                    .map(obj -> obj.getPath()).toArray(String[]::new);
            performed = communicator.sendCards(publicObjNames, toolnames, new String[]{privateObjCard});
            if (!performed) {
                log.addLog("(User:" + user + ") Failed to initialize cards");
                throw new ClientOutOfReachException();
            }
        } catch (Exception e) {
            log.addLog("(User:" + user + ")" + e.getMessage(), e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        try {
            adapter.initializePrivateObjectives(privateObjCard);
            adapter.setPublicObjectives(publicObjectives);
            adapter.setToolCards(toolCards);
            log.addLog("User: " + user + " Tools and Objectives initialized ");

        } catch (ModelException e) {
            log.addLog("", e.getStackTrace());
            throw new ModelException();
        }

    }
    //</editor-fold>

    //<editor-fold desc="End Game Phase">
    public void endGameCommunication(String[] users, int[] points) {
        try {
            communicator.sendResults(users, points);
        } catch (Exception e) {
            log.addLog("(User:" + user + ")" + " Impossible to communicate to user results of match");
        }
    }


    public int getPoints() {
        return adapter.calculatePoints();
    }
    //</editor-fold>

    //<editor-fold desc="Update Client's information">

    /**
     * Send to client a massage that inform about his turn
     */
    private void clientTurn() throws ClientOutOfReachException {
        String u;
        try {
            u = communicator.doTurn();
            if (u == null)
                throw new ClientOutOfReachException();
        } catch (Exception e) {
            log.addLog("(" + user + ") Move timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     * Update Dadiera information on client's side
     */
    private void updateDadiera() throws ClientOutOfReachException {
        String s;
        try {
            s = communicator.updateGraphic(adapter.getDadieraPair());
        } catch (RemoteException e) {
            e.printStackTrace();
            log.addLog("(" + user + ") Dadiera update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     * Update Window information on client's side
     */
    private void updateWindow() throws ClientOutOfReachException {
        String s;
        try {
            s = communicator.updateGraphic(adapter.getWindowPair());
        } catch (RemoteException e) {
            log.addLog("(" + user + ")Window update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    private void updateTokens() throws ClientOutOfReachException {
        String s;
        try {
            s = communicator.updateTokens(adapter.getMarker());
        } catch (RemoteException e) {
            log.addLog("(" + user + ") Token update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    private void updateRoundTrace() throws ClientOutOfReachException {
        String s;
        try {
            s = communicator.updateRoundTrace(adapter.getRoundTracePair());
        } catch (RemoteException e) {
            log.addLog("(" + user + ") Round Trace update timeout expired");
            throw new ClientOutOfReachException();
        }

    }

    public void updateOpponents(ArrayList<String> users, ArrayList<Pair[][]> grids,ArrayList<Boolean> active) throws ClientOutOfReachException {
        for (int i = 0; i < users.size(); i++)
            updateOpponents(users.get(i), grids.get(i),active.get(i));
    }

    /**
     * Update opponent's situation on client's side
     */
    public void updateOpponents(String user, Pair[][] grids,Boolean active) throws ClientOutOfReachException {

        try {
            String r = communicator.updateOpponents(user, grids,active);
            //System.out.println(r);
        } catch (Exception e) {
            log.addLog("", e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    public boolean isClientAlive() {
        try {
            alive = communicator.ping();
        } catch (RemoteException e) {
            alive = false;
        }
        return alive;
    }

    public void sendMessage(String s) {
        try {
            boolean performed;
            performed = communicator.sendMessage(s);
            if (!performed)
                log.addLog("(" + user + ")end message to client failed");
        } catch (Exception ex) {
            log.addLog("Send message to client failed", ex.getStackTrace());
        }
    }

    public void closeConnection(String s) {
        try {
            boolean performed;
            performed = communicator.closeCommunication(s);
            if (!performed)
                log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
            communicator = null;
        } catch (RemoteException | ClientOutOfReachException e) {
            log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }

    }

    public boolean updateClient() {
        boolean exit = true;
        try {
            updateDadiera();
            updateRoundTrace();
            updateWindow();
            updateTokens();
        } catch (Exception e) {
            log.addLog("(" + user + ") Impossible to communicate to client");
            exit = false;
        }
        return exit;
    }

    public Pair[][] getGrid() {
        return adapter.getWindowPair();
    }

    public String getUser() {
        return user;
    }

    /**
     * sets up connection parameters
     */
    private static void parametersSetup() throws ParserXMLException {
        PING_TIMEOUT = ParserXML.SetupParserXML.getPingTimeLaps(FileLocator.getGameSettingsPath());
        SETUP_TIMEOUT = ParserXML.SetupParserXML.getSetupTimeLaps(FileLocator.getGameSettingsPath());
        TURN_TIMEOUT = ParserXML.SetupParserXML.getTurnTimeLaps(FileLocator.getGameSettingsPath());
    }

    //</editor-fold>

    //<editor-fold desc="Set Windows/Objects/Tools">

    /**
     * receives from the match the window cards among which the client need to choose
     *
     * @param c1 first card (two sides)
     * @param c2 second card (two sides)
     */
    public void setWindowCards(String c1[], String c2[]) {
        windowCard1 = c1;
        windowCard2 = c2;
    }

    /**
     * receives from the match the array of the public objectives of the current match
     *
     * @param c array of the public objectives of the current match
     */
    public void setPublicObjCard(PublicObjective[] c) {
        publicObjectives = c;
    }

    /**
     * receives from the match the player's private objective for the current match
     *
     * @param c array of the public objectives of the current match
     */
    public void setPrivateObjCard(String c) {
        privateObjCard = c;
    }

    /**
     * receives from the match the player's tool cards for the current match
     *
     * @param tools array of the tool cards of the current match
     */
    public void setToolCards(Tools[] tools) {
        toolCards = tools;
    }
    //</editor-fold>

    //<editor-fold desc="Timer Utilities">
    public boolean isInGame() {
        return inGame;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setTurnInterrupted() {
        synchronized (interruptionLock) {
            this.turnInterrupted = true;
        }
    }

    public boolean isTurnInterrupted() {
        synchronized (interruptionLock) {
            return turnInterrupted;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Reconnection facilities">
    public void setCommunicator(ClientRemoteInterface communicator) {
        this.communicator = communicator;
    }

    public ClientRemoteInterface getCommunicator() {
        return communicator;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    /**
     * Sends to the client all the information it needs, once it's reconnected
     */
    public void reconnected() {
        try {
            communicator.reconnect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        possibleUsers.setUserGameStatus(user, true);
        String[] toolnames = Arrays.stream(toolCards).map(t -> t.getName()).toArray(String[]::new);
        String[] publicObjNames = Arrays.stream(publicObjectives).map(obj -> obj.getPath()).toArray(String[]::new);
        try {
            communicator.sendCards(publicObjNames, toolnames, new String[]{privateObjCard});
        } catch (ClientOutOfReachException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        assert inGame;
        if (inGame == true) {
            new Thread(this).start();
            token.addPlayer(user);
        }
        log.addLog("User " + user + " back in match");
        //System.out.println("user " + user + " riconnesso");
    }

    public ServerModelAdapter getAdapter() {
        return adapter;
    }
    //</editor-fold>
}
