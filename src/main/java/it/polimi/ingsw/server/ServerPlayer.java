package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.utilities.LogFile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerPlayer extends UnicastRemoteObject implements Runnable,ServerRemoteInterface
{
    private ClientRemoteInterface comunicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;
    private LogFile log;

    //Init phase
    private boolean connectionError;

    //Setup Phase
    private Integer lockObject;
    private ServerSocketHandler socketCon;
    private ArrayList<String> possibleUsers;

    //passed parameters
    private String[] windowCard1,windowCard2;
    private String[] publicObjCard;
    private String privateObjCard;

    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, ArrayList ps,LogFile l) throws RemoteException
    {
        adapter = adp;
        token = tok;
        possibleUsers = ps;
        comunicator = null;
        connectionError = false;
        lockObject = 0;
        log = l;
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
                //initializeCards();
                initializeWindow();
            }
            catch (ClientOutOfReachException|ModelException ex) {
                //Notify token that client is dead
                token.deletePlayer(user);
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
        //RMI Registry creation and bind server name
        try {
            int port = 7000;
            //String hostname = "127.0.0.1";
            String hostname = "192.168.1.5";
            String bindLocation = "rmi://" + hostname + ":" + port + "/sagrada" + progressive;
            try{
                //System.setProperty("java.rmi.server.hostname","192.168.1.1");
                java.rmi.registry.LocateRegistry.createRegistry(port);
            }catch (Exception ex){}

            Naming.bind(bindLocation, this );

            log.addLog("it.polimi.ingsw.server RMI waiting for client on port  " + port);
        }catch (Exception e) {
            log.addLog("RMI Bind failed" , e.getStackTrace());
        }

        //Socket connection creation
        try{
            socketCon = new ServerSocketHandler(log);
            socketCon.createConnection();
            if (socketCon.isConnected())
            {
                comunicator = socketCon;
                log.addLog("Client accepted with Socket connection");
            }
            synchronized (lockObject) {
                lockObject.notifyAll();
            }
        }
        catch (ClientOutOfReachException e) {
            log.addLog(e.getMessage() , e.getStackTrace());
            synchronized (lockObject) {
                connectionError = true;
                lockObject.notifyAll();
            }
        }
        return !connectionError;
    }

    /**
     * From this method client can connect with RMI
     * @param client client stub
     */
    public void setClient (ClientRemoteInterface client) throws RemoteException
    {
        synchronized (lockObject)
        {
            socketCon.start();
            comunicator = client;
            lockObject.notifyAll();
            log.addLog("Client accepted with RMI connection");
        }

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
                u = comunicator.login();
            } while (!possibleUsers.contains(u));
            possibleUsers.remove(u);
            user = u;
            log.addLog("User: " + user + " Added");
        }
        catch (ClientOutOfReachException|RemoteException e) {
            log.addLog("Failed to add user" , e.getStackTrace());
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
        String s1 ="";
        try {
            s1 = comunicator.chooseWindow(windowCard1, windowCard2);
        }
        catch (ClientOutOfReachException|RemoteException e) {
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
            log.addLog(ex.getMessage());
            throw new ModelException();
        }
    }

    private void initializePrivateObjectives ()
    {
        //Comunicazione col client per la sua carta obbiettivo privato
    }

    /**
     * receives the public objectives and the tools chosen for the current match
     * and sets the corresponding parameters on the model adapter
     * @throws ClientOutOfReachException
     */
    private void initializeCards () throws ClientOutOfReachException
    {
        try {
            comunicator.sendCards(publicObjCard);
        }
        catch (ClientOutOfReachException|RemoteException e) {
            log.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    public boolean isClientAlive ()
    {
        try {
            //setTimeout(50000);
            return comunicator.ping();
        }catch (RemoteException e) {
            return false;
        }
    }

    public void sendMessage (String s)
    {
        try {
            comunicator.sendMessage(s);
        }catch (Exception ex) {
            log.addLog("Send message to client failed", ex.getStackTrace());
        }

    }

    //It works but on login function it doesn't wait client's response
    /*private  void setTimeout (int milli)
    {
        try {
            RMISocketFactory.setSocketFactory(new RMISocketFactory()
            {
                public Socket createSocket(String host, int port )
                        throws IOException
                {
                    Socket socket = new Socket();
                    socket.setSoTimeout( milli );
                    socket.connect( new InetSocketAddress( host, port ), milli );
                    return socket;
                }

                public ServerSocket createServerSocket(int port )
                        throws IOException
                {
                    return new ServerSocket( port );
                }
            } );

        }catch (IOException e){
            log.addLog("Impossible to set RMI timeout");
        }
    }*/

    public void closeCommunication ()
    {

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


}
