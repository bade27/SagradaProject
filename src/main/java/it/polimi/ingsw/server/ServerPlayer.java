package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.UsersEntry;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

public class ServerPlayer implements Runnable
{
    //connection parameters
    private static int PING_TIMEOUT; //10 sec
    private static int INIT_TIMEOUT;
    private static int TURN_TIMEOUT; //5 min

    private ClientRemoteInterface communicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;
    private ExecutorService executor;

    //Setup Phase
    private UsersEntry possibleUsers;
    private boolean inGame;
    private boolean initialized;

    //passed parameters
    private String[] windowCard1,windowCard2;
    private String privateObjCard;

    //actual cards
    private Tools[] toolCards;
    private PublicObjective[] publicObjectives;


    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, UsersEntry ps, ClientRemoteInterface cli)
    {
        adapter = adp;
        token = tok;
        possibleUsers = ps;
        communicator = null;
        executor = Executors.newFixedThreadPool(1);
        communicator = cli;
        inGame = true;
        initialized = false;
        try {
            connection_parameters_setup();
        } catch (ParserConfigurationException| IOException | SAXException e) {
            LogFile.addLog("Impossible to read settings parameters" , e.getStackTrace());
        }
    }

    public synchronized void run ()
    {
        //////SETUP PHASE//////
        try
        {
            synchronized (token.getSynchronator()) {
                //Wait until matchHandler signal start setup
                while (!token.getOnSetup())
                    token.getSynchronator().wait();
            }

            //Initialization of client
            try {
                login();
                token.addPlayer(user);
                initializeWindow();
                initializeCards();
            }
            catch (ClientOutOfReachException|ModelException ex) {
                //Notify token that client is dead
                token.deletePlayer(user);
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
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            LogFile.addLog("Fatal error on thread " + user  , ex.getStackTrace());
            token.notifyFatalError();
            Thread.currentThread().interrupt();
        }

        //////GAME PHASE//////
        while (inGame)//Da cambiare con la condizione di fine partita
        {
            synchronized (token.getSynchronator())
            {
                try
                {
                    //Wait his turn
                    while (!token.isMyTurn(user))
                        token.getSynchronator().wait();

                    if (token.isEndGame())
                    {
                        LogFile.addLog("(User:" + user + ")" + " End communication with client and close connection");
                        token.getSynchronator().notifyAll();
                        return;
                    }

                    LogFile.addLog("Turn of:" + user);
                    System.out.println("\n>>>Turn of:" + user);

                    try {
                        adapter.setCanMove(true);
                        clientTurn();
                    }catch (ClientOutOfReachException e){
                        //Notify token that client is dead
                        token.deletePlayer(user);
                        inGame = false;
                        token.getSynchronator().notifyAll();
                        break;
                    }

                    //End turn comunication
                    synchronized (adapter){
                        adapter.wait();
                    }

                    token.getSynchronator().notifyAll();
                    token.getSynchronator().wait();
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                    LogFile.addLog("Fatal error on thread " + user  , ex.getStackTrace());
                    token.notifyFatalError();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    //<editor-fold desc="Setup Phase">
    /**
     * Initialize username through login phase
     * @throws ClientOutOfReachException client is out of reach
     */
    private void login () throws ClientOutOfReachException
    {
        String u;
        try{
            do{
                u = stopTask(() -> communicator.login(), INIT_TIMEOUT, executor);
                if(u == null)
                {
                    LogFile.addLog("Failed to add user");
                    throw new ClientOutOfReachException();
                }
            } while (!possibleUsers.loginCheck(u));
            user = u;
            adapter.setUser(u);
            LogFile.addLog("User: " + user + " Added");
        }
        catch (Exception e) {
            LogFile.addLog("Failed to add user");
            throw new ClientOutOfReachException();
        }
    }

    /**
     * Initialize client's window board
     * @throws ClientOutOfReachException client is out of reach
     * @throws ModelException Impossible to set window
     */
    private void initializeWindow () throws ClientOutOfReachException,ModelException
    {
        String s1;
        try {
            s1 = stopTask(() -> communicator.chooseWindow(windowCard1, windowCard2), INIT_TIMEOUT, executor);//To change with ACTION when implement user choice
            if(s1 == null)
            {
                LogFile.addLog("(User:" + user + ") Failed to initialize Windows");
                throw new ClientOutOfReachException();
            }
        }
        catch (Exception e) {
            LogFile.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        if(s1.equals(""))
            throw new ModelException("void field");

        try {
            adapter.initializeWindow(s1);
            LogFile.addLog("User: " + user + " Window initialized ");
        }
        catch (ModelException ex) {
            LogFile.addLog("Impossible to set Window from XML", ex.getStackTrace());
            throw new ModelException();
        }
    }

    /**
     * receives the public objectives and the tools chosen for the current match
     * and sets the corresponding parameters on the model adapter
     */
    private void initializeCards () throws ClientOutOfReachException,ModelException
    {
        try {
            boolean performed;
            String[] toolnames = Arrays.stream(toolCards).map(t -> t.getName()).toArray(String[]::new);
            String[] publicObjNames = Arrays.stream(publicObjectives).map(obj -> obj.getPath()).toArray(String[]::new);
            performed = stopTask(() -> communicator.sendCards(publicObjNames,toolnames,new String[] {privateObjCard}), INIT_TIMEOUT, executor);
            if(!performed)
            {
                LogFile.addLog("(User:" + user + ") Failed to initialize cards");
                throw new ClientOutOfReachException();
            }
        }
        catch (Exception e) {
            LogFile.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        try {
            adapter.initializePrivateObjectives(privateObjCard);
            adapter.setPublicObjectives(publicObjectives);
            adapter.setToolCards(toolCards);
            LogFile.addLog("User: " + user + " Tools and Objectives initialized ");

        }catch (ModelException e ){
            LogFile.addLog("", e.getStackTrace());
            throw new ModelException();
        }

    }
    //</editor-fold>


    //<editor-fold desc="End Game Phase">
    public void endGameCommunication (String [] users, int [] points)
    {
        try {
            communicator.sendResults(users,points);
        }
        catch (Exception e) {
            LogFile.addLog("(User:" + user + ")" + " Impossible to communicate to user results of match");
        }
    }


    public int getPoints ()
    {
        return adapter.calculatePoints();
    }
    //</editor-fold>


    //<editor-fold desc="Update Client's information">
    /**
     * Send to client a massage that inform about his turn
     */
    private void clientTurn () throws ClientOutOfReachException
    {
        String u;
        try{
            u = stopTask(() -> communicator.doTurn(), TURN_TIMEOUT, executor);
            if(u == null)
                throw new ClientOutOfReachException();
        }
        catch (Exception e) {
            LogFile.addLog("(" + user + ") Move timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     *  Update Dadiera information on client's side
     */
    private void updateDadiera () throws ClientOutOfReachException
    {
        String s;
        try{
            s = stopTask(() -> communicator.updateGraphic(adapter.getDadieraPair()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        }
        catch (Exception e) {
            LogFile.addLog("(" + user + ") Dadiera update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    /**
     *  Update Window information on client's side
     */
    private void updateWindow () throws ClientOutOfReachException
    {
        String s;
        try{
            s = stopTask(() -> communicator.updateGraphic(adapter.getWindowPair()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        }
        catch (Exception e) {
            LogFile.addLog("(" + user + ")Window update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    private void updateTokens () throws ClientOutOfReachException
    {
        String s;
        try{
            s = stopTask(() -> communicator.updateTokens(adapter.getMarker()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        }
        catch (Exception e) {
            LogFile.addLog("(" + user + ") Token update timeout expired");
            throw new ClientOutOfReachException();
        }
    }

    private void updateRoundTrace () throws ClientOutOfReachException
    {
        String s;
        try{
            s = stopTask(() -> communicator.updateRoundTrace(adapter.getRoundTracePair()), PING_TIMEOUT, executor);
            if(s == null)
                throw new ClientOutOfReachException();
        }
        catch (Exception e) {
            LogFile.addLog("(" + user + ") Round Trace update timeout expired");
            throw new ClientOutOfReachException();
        }

    }

    /**
     *  Update opponent's situation on client's side
     */
    public void updateOpponents(String user, Pair[][] grids) throws ClientOutOfReachException {

        try{
            stopTask(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    communicator.updateOpponents(user,grids);
                    return null;
                }
            }, PING_TIMEOUT, executor);
        } catch (Exception e) {
            LogFile.addLog("" , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    public boolean isClientAlive ()
    {
        try {
            boolean performed;
            performed = stopTask(() -> communicator.ping(), PING_TIMEOUT, executor);
            return performed;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void sendMessage (String s)
    {
        try {
            boolean performed;
            performed = stopTask(() -> communicator.sendMessage(s), PING_TIMEOUT, executor);
            if (!performed)
                LogFile.addLog("(" + user + ")end message to client failed");
        }catch (Exception ex) {
            LogFile.addLog("Send message to client failed", ex.getStackTrace());
        }
    }

    public void closeConnection (String s)
    {
        try {
            boolean performed;
            performed = stopTask(() -> communicator.closeCommunication(s), PING_TIMEOUT, executor);
            if (!performed)
                LogFile.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }catch (NullPointerException e) {
            LogFile.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }

    }

    public boolean updateClient ()
    {
        boolean exit = true;
        try{
            updateDadiera();
            updateRoundTrace();
            updateWindow();
            updateTokens();
        }catch (Exception e){
            LogFile.addLog("(" + user + ") Impossible to communicate to client");
            exit = false;
        }
        return exit;
    }

    public Pair[][] getGrid ()
    {
        return adapter.getWindowPair();
    }

    public String getUser ()
    {
        return user;
    }

    /**
     * sets up connection parameters
     */
    private static void connection_parameters_setup() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(FileLocator.getServerSettingsPath());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        //SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
        PING_TIMEOUT = Integer.parseInt(document.getElementsByTagName("ping").item(0).getTextContent());
        INIT_TIMEOUT = Integer.parseInt(document.getElementsByTagName("init").item(0).getTextContent());
        TURN_TIMEOUT = Integer.parseInt(document.getElementsByTagName("turn").item(0).getTextContent());
    }

    //</editor-fold>

    //<editor-fold desc="Set Windows/Objects/Tools">
    /**
     * receives from the match the window cards among which the client need to choose
     * @param c1 first card (two sides)
     * @param c2 second card (two sides)
     */
    public void setWindowCards (String c1[],String c2 [])
    {
        windowCard1 = c1;
        windowCard2 = c2;
    }

    /**
     * receives from the match the array of the public objectives of the current match
     * @param c array of the public objectives of the current match
     */
    public void setPublicObjCard (PublicObjective[] c)
    {
        publicObjectives = c;
    }

    /**
     * receives from the match the player's private objective for the current match
     * @param c array of the public objectives of the current match
     */
    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
    }

    /**
     * receives from the match the player's tool cards for the current match
     * @param tools array of the tool cards of the current match
     */
    public void setToolCards (Tools[] tools)
    {
        toolCards = tools;
    }
    //</editor-fold>

    //<editor-fold desc="Executor">
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
        return (T)o;
    }

    public boolean isInGame() {
        return inGame;
    }

    public boolean isInitialized()
    {
        return initialized;
    }
    //</editor-fold>

}
