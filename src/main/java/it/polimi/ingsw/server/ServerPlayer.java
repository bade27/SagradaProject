package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.utilities.LogFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ServerPlayer extends UnicastRemoteObject implements Runnable,ServerRemoteInterface
{
    //connection parameters
    private static final String settings = "resources/server_settings.xml";
    private static int RMI_REGISTRY_PORT;
    private static int RMI_STUB_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private static int PING_TIMEOUT; //10 sec
    private static int INIT_TIMEOUT;
    private static int TURN_TIMEOUT; //5 min

    private ClientRemoteInterface communicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;
    private LogFile log;
    private ExecutorService executor;

    //Init phase
    private boolean connectionError;

    //Setup Phase
    private ServerSocketHandler socketCon;
    private ArrayList<String> possibleUsers;

    //passed parameters
    private String[] windowCard1,windowCard2;
    private String[] publicObjCard;
    private String privateObjCard;

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
        RMI_STUB_PORT = Integer.parseInt(document.getElementsByTagName("stubPort").item(0).getTextContent());
        HOSTNAME = document.getElementsByTagName("hostName").item(0).getTextContent();

        //socket setup
        SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
        PING_TIMEOUT = Integer.parseInt(document.getElementsByTagName("ping").item(0).getTextContent());
        INIT_TIMEOUT = Integer.parseInt(document.getElementsByTagName("init").item(0).getTextContent());
        TURN_TIMEOUT = Integer.parseInt(document.getElementsByTagName("turn").item(0).getTextContent());
    }

    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, ArrayList ps,LogFile l) throws RemoteException
    {
        adapter = adp;
        token = tok;
        possibleUsers = ps;
        communicator = null;
        connectionError = false;
        log = l;
        executor = Executors.newCachedThreadPool();
    }



    public synchronized void run ()
    {
        //////SETUP PHASE//////
        try
        {
            synchronized (token) {
                //Wait until matchHandler signal start setup
                while (!token.getOnSetup())
                    token.wait();
            }

            //Initialization of client
            try {
                login();
                token.addPlayer(user);
                initializeCards();
                initializeWindow();
            }
            catch (ClientOutOfReachException|ModelException ex) {
                //Notify token that client is dead
                token.deletePlayer(user);
                closeConnection("Timeout Expired");
                token.endSetup();
                synchronized (token) {
                    token.notifyAll();
                }
                //If client fail initialization, he will not return on game
                return;
            }

            //End Setup phase comunication
            token.endSetup();
            synchronized (token) {
                token.notifyAll();
            }
        }
        catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
            log.addLog("Fatal error on thread " + user  , ex.getStackTrace());
            token.notifyFatalError();
            Thread.currentThread().interrupt();
        }

        //////GAME PHASE//////
        while (true)//Da cambiare con la condizione di fine partita
        {
            synchronized (token)
            {
                try
                {
                    //Wait his turn
                    while (!token.isMyTurn(user))
                        token.wait();

                    //Simulazione del turno
                    log.addLog("Turn of:" + user);
                    System.out.println(">>>Turn of:" + user);
                    Thread.sleep(2000);

                    //Per attivare il turno decommentare sotto e commentare la sleep
                    /*try
                    {
                        clientTurn();
                    }catch (ClientOutOfReachException e){
                        //Notify token that client is dead
                        token.deletePlayer(user);
                        closeConnection("Timeout Expired");
                        token.endSetup();
                        synchronized (token) {
                            token.notifyAll();
                        }
                    }*/


                    //End turn comunication
                    token.notifyAll();
                    token.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    log.addLog("Fatal error on thread " + user  , ex.getStackTrace());
                    token.notifyFatalError();
                    Thread.currentThread().interrupt();
                }

            }
        }
    }



    //<editor-fold desc="Initialization Phase">
    /**
     * Generate 2 method for accepting client (Rmi and Socket)
     * For socket this method creates a thread in waiting of Connection client
     * For RMI this method put a bind of ServerPlayer
     * @return true if connection goes well, false otherwise
     */
    public boolean initializeCommunication (int progressive)
    {
        try {
            connection_parameters_setup();
        } catch (ParserConfigurationException| IOException | SAXException e) {
            log.addLog("Impossible to read settings parameters" , e.getStackTrace());
        }

        //RMI Registry creation and bind server name
        try {
            System.setProperty("sun.rmi.transport.connectionTimeout" , "1000");
            String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
            try{
                java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            }catch (Exception ex){}

            Naming.bind(bindLocation, this );

            log.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
        }catch (Exception e) {
            log.addLog("RMI Bind failed" , e.getStackTrace());
        }

        //Socket connection creation
        try{
            socketCon = new ServerSocketHandler(log, SOCKET_PORT);
            socketCon.createConnection();
            if (socketCon.isConnected())
            {
                communicator = socketCon;
                log.addLog("Client accepted with Socket connection");
            }
        }
        catch (ClientOutOfReachException e) {
            log.addLog(e.getMessage() , e.getStackTrace());

            connectionError = true;
        }
        return !connectionError;
    }

    /**
     * From this method client can connect with RMI
     * @param client client stub
     */
    public void setClient (ClientRemoteInterface client) throws RemoteException
    {
        //Stop accepting of socket
        socketCon.start();
        //Set client stub
        communicator = client;
        log.addLog("Client accepted with RMI connection");


    }
    //</editor-fold>

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
                    log.addLog("Failed to add user");
                    throw new ClientOutOfReachException();
                }
            } while (!possibleUsers.contains(u));
            possibleUsers.remove(u);
            user = u;
            log.addLog("User: " + user + " Added");
        }
        catch (Exception e) {
            log.addLog("" , e.getStackTrace());
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
                log.addLog("(User:" + user + ") Failed to initialize Windows");
                throw new ClientOutOfReachException();
            }
        }
        catch (Exception e) {
            log.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        if(s1.equals(""))
            throw new ModelException("void field");

        try {
            adapter.initializeWindow(s1);
            log.addLog("User: " + user + " Window initialized: " + s1);
        }
        catch (ModelException ex) {
            log.addLog("Impossible to set Window from XML", ex.getStackTrace());
            throw new ModelException();
        }
    }

    /**
     * receives the public objectives and the tools chosen for the current match
     * and sets the corresponding parameters on the model adapter
     */
    private void initializeCards () throws ClientOutOfReachException
    {
        try {
            boolean performed;
            performed = stopTask(() -> communicator.sendCards(publicObjCard), INIT_TIMEOUT, executor);
            if(!performed)
            {
                log.addLog("(User:" + user + ") Failed to initialize cards");
                throw new ClientOutOfReachException();
            }
        }
        catch (Exception e) {
            log.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    private void clientTurn () throws ClientOutOfReachException
    {
        String u;
        try{
            u = stopTask(() -> communicator.doTurn(), TURN_TIMEOUT, executor);
            if(u == null)
            {
                log.addLog(" Move timeout expired");
                throw new ClientOutOfReachException();
            }
            System.out.println(u);
        }
        catch (Exception e) {
            log.addLog("" , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

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
                log.addLog("Send message to client failed");
        }catch (Exception ex) {
            log.addLog("Send message to client failed", ex.getStackTrace());
        }
    }

    public void closeConnection (String s)
    {
        try {
            boolean performed;
            performed = stopTask(() -> communicator.closeCommunication(s), PING_TIMEOUT, executor);
            if (!performed)
                log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }catch (NullPointerException e) {
            e.printStackTrace();
            log.addLog("Impossible to communicate to client (" + user + ") cause closed connection");
        }

    }
    //</editor-fold>

    //<editor-fold desc="Set Windows/Objects/Tools">
    /**
     * receives from the match the window cards among which the client need to choose
     * and sends them to the client
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
     * and sends them to the client
     * @param c array of the public objectives of the current match
     */
    public void setPublicObjCard (String[] c)
    {
        publicObjCard = c;
    }

    /**
     * receives from the match the player's private objective for the current match
     * and sends them to the client
     * @param c array of the public objectives of the current match
     */
    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
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
            log.addLog("Client too late to reply");
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
    //</editor-fold>
}
