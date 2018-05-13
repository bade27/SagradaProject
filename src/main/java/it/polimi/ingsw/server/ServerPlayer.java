package it.polimi.ingsw.server;

import it.polimi.ingsw.RemoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.RemoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.utilities.LogFile;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerPlayer extends UnicastRemoteObject implements Runnable,ServerRemoteInterface
{
    private ClientRemoteInterface comunicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;

    private boolean connectionError;
    private Integer lockObject;
    ServerConnectionHandler socketCon;

    private ArrayList<String> possibleUsers;

    private String[] windowCard1,windowCard2,publicObjCard;
    private String privateObjCard;

    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, ArrayList ps) throws RemoteException
    {
        adapter = adp;
        token = tok;
        possibleUsers = ps;
        comunicator = null;
        connectionError = false;
        lockObject = 0;
        //initConn();
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
            LogFile.addLog("Fatal error on thread " + user  , ex.getStackTrace());
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
                    LogFile.addLog("Turn of:" + user);
                    System.out.println(">>>Turn of:" + user);
                    Thread.sleep(2000);

                    //End turn comunication
                    token.notifyAll();
                    token.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    LogFile.addLog("Fatal error on thread " + user  , ex.getStackTrace());
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
            try{
                java.rmi.registry.LocateRegistry.createRegistry(1099);
            }catch (Exception ex){}

            Naming.bind("rmi://127.0.0.1/sagrada" + progressive, this );

            LogFile.addLog("RMI Bind Waiting for client");
        }catch (Exception e) {
            LogFile.addLog("RMI Bind failed" , e.getStackTrace());
        }

        //Socket connection creation
        try{
            socketCon = new ServerConnectionHandler();
            socketCon.createConnection();
            if (socketCon.isConnected())
                comunicator = socketCon;
            synchronized (lockObject) {
                lockObject.notifyAll();
            }
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog(e.getMessage() , e.getStackTrace());
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
        }

    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">
    /**
     * Initialize username through login phase
     * @throws ClientOutOfReachException it.polimi.ingsw.client is out of reach
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
            LogFile.addLog("User: " + user + " Added");
        }
        catch (ClientOutOfReachException|RemoteException e) {
            LogFile.addLog(e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    /**
     * Initialize client's window board
     * @throws ClientOutOfReachException it.polimi.ingsw.client is out of reach
     * @throws ModelException Impossible to set window
     */
    private void initializeWindow () throws ClientOutOfReachException,ModelException
    {
        String s1 ="";
        try {
            s1 = comunicator.chooseWindow(windowCard1, windowCard2);
        }
        catch (ClientOutOfReachException|RemoteException e) {
            LogFile.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        if(s1.equals(""))
            throw new ModelException("void field");

        try {
            adapter.initializeWindow(s1);
            LogFile.addLog("User: " + user + " Window initialized: " + s1);
        }
        catch (ModelException ex) {
            LogFile.addLog(ex.getMessage());
            throw new ModelException();
        }
    }

    private void initializePrivateObjectives (String card)
    {
        //Comunicazione col client per la sua carta obbiettivo privato
    }

    private void initializePublicObjectives (String[] cards)
    {
        //Comunicazione col client per le carte obbiettivo pubblico
    }
    //</editor-fold>

    //<editor-fold desc="Utilities: Ping/CloseCommunication(to implement)">
    public boolean isClientAlive ()
    {
        try {
            return comunicator.ping();
        }catch (RemoteException e) {
            return false;
        }

    }
    public void closeComunication ()
    {

    }
    //</editor-fold>

    //<editor-fold desc="Window and objects set">
    public void setWindowCards (String c1[],String c2 [])
    {
        windowCard1 = c1;
        windowCard2 = c2;
    }

    public void setPublicObjCard (String[] c)
    {
        publicObjCard = c;
    }

    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
    }
    //</editor-fold>

}
