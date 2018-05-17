package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.utilities.LogFile;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerPlayer extends UnicastRemoteObject implements Runnable,ServerRemoteInterface
{
    private ClientRemoteInterface comunicator;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;
    private LogFile log;

    private boolean connectionError;

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
                System.setProperty("java.rmi.server.hostname","192.168.1.1");
                java.rmi.registry.LocateRegistry.createRegistry(1099);
            }catch (Exception ex){}

            Naming.bind("rmi://0.0.0.0:1099/sagrada" + progressive, this );

            log.addLog("RMI Bind Waiting for client");
        }catch (Exception e) {
            log.addLog("RMI Bind failed" , e.getStackTrace());
        }

        //Socket connection creation
        try{
            socketCon = new ServerSocketHandler(log);
            socketCon.createConnection();
            if (socketCon.isConnected())
                comunicator = socketCon;
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
            //try
            //{
                socketCon.start();
                //socketCon.join();
                comunicator = client;
                lockObject.notifyAll();
            //}catch (InterruptedException e){
                //throw new RemoteException();
            //}

        }

    }

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


    public boolean isClientAlive ()
    {
        try {
            return comunicator.ping();
        }catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

    }
    public void closeComunication ()
    {

    }

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


}
