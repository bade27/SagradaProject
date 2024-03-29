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
    private Boolean alive;

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

    private boolean inTurn;


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
        while (inGame)
        {
            turnInterrupted = false;
            inTurn = false;
            synchronized (token.getSynchronator()) {
                try {
                    //Wait his turn
                    while (!token.isMyTurn(user))
                        token.getSynchronator().wait();

                    if(!inGame) {
                        token.deletePlayer(user);
                        break;
                    }

                    adapter.setTurnDone(false);

                    if (token.isEndGame()) {
                        log.addLog("(User:" + user + ")"
                                + " End communication with client and close connection");
                        token.getSynchronator().notifyAll();
                        return;
                    }

                    log.addLog("Turn of:" + user);

                    try {
                        adapter.setCanMove(true);
                        clientTurn();
                    } catch (ClientOutOfReachException e) {
                        setPlayerAsOffline();
                        token.deletePlayer(user);
                        token.getSynchronator().notifyAll();
                        break;
                    }

                    inTurn = true;
                    adapter.setTimer(TURN_TIMEOUT);
                    adapter.startTimer();

                    //End turn comunication
                    synchronized (adapter) {
                        adapter.wait();
                    }

                    if (isTurnInterrupted()) {
                        setPlayerAsOffline();
                        token.deletePlayer(user);
                        token.getSynchronator().notifyAll();
                        break;
                    }

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

    public void setPlayerAsOffline() {
        ((ServerCommunicator)communicator).close();
        communicator = null;
        log.addLog("(User: " + user + ")" +
                "player disconnected cause client late in response");
        possibleUsers.setUserGameStatus(user, false);
        mymatch.incDisconnCounter();
        mymatch.subFromnConn(1);
        inGame = false;
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
            String[] toolnames = Arrays.stream(toolCards).map(t -> t.getName()).toArray(String[]::new);
            String[] publicObjNames = Arrays.stream(publicObjectives).map(obj -> obj.getPath()).toArray(String[]::new);
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

    /**
     * Notifies the clients about the end of the game
     * @param users list of users that have played
     * @param points list of the scores of each player
     */
    public void endGameCommunication(String[] users, int[] points) {
        try {
            communicator.sendResults(users, points);
        } catch (Exception e) {
            log.addLog("(User:" + user + ")" + " Impossible to communicate to user results of match");
        }
    }


    /**
     * @return the total score of tha player
     */
    public int getPoints() {
        return adapter.calculatePoints();
    }
    //</editor-fold>

    //<editor-fold desc="Update Client's information">

    /**
     * updates the client with all the information he/she needs
     * @return weather the update was successful or not
     */
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


    /**
     * Send to client a massage that informs about his turn
     */
    private void clientTurn() throws ClientOutOfReachException {
        String u;
        try {
            u = stopTask(() -> communicator.doTurn(), PING_TIMEOUT, executor);
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
            s = stopTask(() -> communicator.updateGraphic(adapter.getDadieraPair()), PING_TIMEOUT + 10000, executor);
            if(s == null) {
                throw new ClientOutOfReachException();
            }
        } catch (Exception e) {

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
            s = stopTask(() -> communicator.updateGraphic(adapter.getWindowPair()), PING_TIMEOUT, executor);
            if(s == null) {
                throw new ClientOutOfReachException();
            }
        } catch (Exception e) {
            log.addLog("(" + user + ")Window update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     * updates the number of tokens left to the player
     * @throws ClientOutOfReachException if the client is unreachable
     */
    private void updateTokens() throws ClientOutOfReachException {
        String s;
        try {
            s = stopTask(() -> communicator.updateTokens(adapter.getMarker()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        } catch (Exception e) {
            log.addLog("(" + user + ") Token update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     * updates the client about the content of the round trace
     * @throws ClientOutOfReachException if the client is unreachable
     */
    private void updateRoundTrace() throws ClientOutOfReachException {
        String s;
        try {
            s = stopTask(() -> communicator.updateRoundTrace(adapter.getRoundTracePair()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        } catch (Exception e) {
            log.addLog("(" + user + ") Round Trace update timeout expired");
            throw new ClientOutOfReachException();
        }

    }

    /**
     * updates the client about the other players' board
     * @throws ClientOutOfReachException if the client is unreachable
     */
    public void updateOpponents(ArrayList<String> users, ArrayList<Pair[][]> grids,ArrayList<Boolean> active) throws ClientOutOfReachException {
        for (int i = 0; i < users.size(); i++)
            updateOpponents(users.get(i), grids.get(i),active.get(i));
    }

    /**
     * Update opponent's situation on client's side
     */
    public void updateOpponents(String user, Pair[][] grids,Boolean active) throws ClientOutOfReachException {

        try {
            String r = stopTask(() -> communicator.updateOpponents(user, grids,active), PING_TIMEOUT, executor);
            if(r == null)
                throw new ClientOutOfReachException();
        } catch (Exception e) {
            log.addLog("", e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">

    /**
     * pings the client to see if he/she's still connected
     * @return weather the client is still reachable or not
     */
    public boolean isClientAlive() {
        alive = stopTask(() -> communicator.ping(), PING_TIMEOUT, executor);
        if(alive == null) {
            alive = false;
        }
        return alive;
    }

    /**
     * updates the client with the exit of his/hers action(s)
     * @param s the message to be sent
     */
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

    /**
     * due to various errors (internal server errors, or end of any timeout) ends the connection with the client
     * @param s the message to be sent
     */
    public void closeConnection(String s) {
        try {
            boolean performed;
            performed = communicator.closeCommunication(s);
            if (!performed)
                log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
            ((ServerCommunicator)communicator).close();
            communicator = null;
        } catch (RemoteException | ClientOutOfReachException e) {
            log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }

    }

    /**
     * @return the board's content in form of Pair matrix
     * (this content is about colors and numbers of the dice)
     */
    public Pair[][] getGrid() {
        return adapter.getWindowPair();
    }

    public String getUser() {
        assert user != null;
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

    public void setCommunicator(ClientRemoteInterface communicator) {
        this.communicator = communicator;
    }

    public ClientRemoteInterface getCommunicator() {
        return communicator;
    }

    /**
     * sets the status of the client
     * @param inGame
     */
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
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

    /**
     * executes the given task and retrieves the results after a fixed amount of time
     * @param task task to be performed
     * @param executionTime maximum time of the execution
     * @param executor thread pool
     * @param <T> type of the parameter returned by the task
     * @return the result of the task
     */
    private <T> T stopTask(Callable<T> task, int executionTime, ExecutorService executor) {
        Object o = null;
        Future future = executor.submit(task);
        try {
            o = future.get(executionTime, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            //System.out.println(te.getMessage());
            //LogFile.addLog("Client too late to reply");
            //System.out.println("too late to reply");
        } catch (InterruptedException ie) {
            //System.out.println(ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            //System.out.println(ee.getMessage());
        } finally {
            future.cancel(true);
        }
        return (T) o;
    }

    public boolean isInTurn() {
        return inTurn;
    }

    public ServerModelAdapter getAdapter() {
        return adapter;
    }

    //</editor-fold>

    //<editor-fold desc="Disconnection and Reconnection facilities">

    /**
     * Sends to the client all the information it needs, once it's reconnected
     */
    public void reconnected() {
        try {
            communicator.reconnect();
        } catch (RemoteException e) {
            log.addLog("User " + user + " cannot be set back in match\n" + e.getStackTrace());
            return;
        }
        possibleUsers.setUserGameStatus(user, true);
        try {
            initializeCards();
        } catch (ClientOutOfReachException|ModelException e) {
            log.addLog("User " + user + " cannot be set back in match\n" + e.getStackTrace());
            return;
        }
        assert inGame;
        if (inGame == true) {
            new Thread(this).start();
            if(!token.getPlayers().contains(user))
                token.addPlayer(user);
        }
        log.addLog("User " + user + " back in match");
    }

    //</editor-fold>

}
