package it.polimi.ingsw.client;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.ServerModelAdapter;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ClientPlayer extends UnicastRemoteObject implements ClientRemoteInterface
{
    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static int TURN_TIMEOUT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private static int STATUS_TIMEOUT;

    private static boolean initialized = false;
    private int typeOfCOnnection; //1 rmi , 0 Socket
    private UI graph;
    private ServerRemoteInterface server;
    private String clientName;
    private String chooseMap;

    //buffer mossa in upload
    private boolean finishedMove = false;
    private final Object synclogin = new Object();
    private final Object syncmap = new Object();

    //to keep trak of server status. used ONLY with RMI
    private Timer connectionStatusRMITimer;
    private Thread timerTurn;

    private boolean connected;
    private boolean inTurn;
    private final Object lockInTurn = new Object();

    private boolean cannotLogIn = false;
    private boolean serverAlive;


    //<editor-fold desc="Initialization Phase">

    public ClientPlayer (int t, UI g, String serverIP) throws RemoteException
    {
        connected = false;
        if(!serverIP.isEmpty())
            HOSTNAME = serverIP;
        typeOfCOnnection = t;
        this.graph = g;
        setServerAlive(true);
    }


    /**
     * establishes the connection with the server
     */
    public void connect() {
        try
        {
            //since the parameters are static, the initialization is performed once
            if(!initialized) {
                connection_parameters_setup();
                System.out.println(HOSTNAME);
                initialized = true;
            }

            //if connection is socket, creates socket connect
            if (typeOfCOnnection == 0)
                server = new ClientSocketHandler(this, HOSTNAME, SOCKET_PORT);

                //if connection is RMI, creates RMI lookup of stub
            else if (typeOfCOnnection == 1)
            {
                String remote = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT;
                System.out.println("RMI connection to host " + HOSTNAME + " port " + RMI_REGISTRY_PORT +  "...");
                String[] e = Naming.list(remote);

                String s = Collections.max(new ArrayList<>(Arrays.asList(e)));

                server=(ServerRemoteInterface) Naming.lookup(s);
                server.setClient(this);
            }

        }
        catch (ClientOutOfReachException e){
            System.out.println("Impossible to connect to Host with socket");
            //e.printStackTrace();
            graph.login("Impossibile connettersi al server, riprova");
            return;
        }
        catch (RemoteException e){
            //e.printStackTrace();
            System.out.println("Impossible to connect to Host with RMI");
            graph.login("Impossibile connettersi al server, riprova");
            return;
        }
        catch (Exception e){
            //e.printStackTrace();
            System.out.println("Impossibile connettersi al server, riprova");
            return;
        }

        System.out.println("Client connected");
        connected = true;
        setInTurn(false);


        connectionStatusRMITimer = new Timer();
        connectionStatusRMITimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isServerAlive() && !isInTurn())
                {
                    closeCommunication("Il server non risponde");
                    //System.exit(0);
                }
                setServerAlive(false);
            }
        }, 0,STATUS_TIMEOUT * 1000);
    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">
    /**
     * Request to user a username
     * @return username inserted
     */
    public String login() throws ClientOutOfReachException
    {
        setServerAlive(true);
        if (cannotLogIn)
        {
            graph.login("nome già esistente");

            try {
                synchronized (synclogin)
                {
                    while (cannotLogIn)
                        synclogin.wait();
                }
            } catch (InterruptedException e) {
                return null;
            }
        }

        String ret = clientName;
        cannotLogIn = true;
        return ret;
    }

    /**
     * Set Client name
     * @param clientName
     */
    public void setClientName(String clientName)
    {
        this.cannotLogIn = false;
        this.clientName = clientName;
        synchronized (synclogin) {
            synclogin.notifyAll();
        }
    }

    /**
     * Choosing window method
     */
    public String chooseWindow(ArrayList<String[]> list)
    {
        String choice;
        choice = chooseWindow(list.get(0),list.get(1));
        return choice;
    }

    /**
     * Real Choosing window method
     */
    public String chooseWindow(String[] s1, String[] s2)
    {
        if (chooseMap == null)
        {
            try {
                graph.maps(s1, s2);
            } catch (Exception e) {
                graph.fatalDisconnection("Errore interno");
            }
            try {
                synchronized (syncmap) {
                    while (chooseMap == null)
                        syncmap.wait();
                }
            } catch (Exception e) {
                return null;
            }
        }

        String m = chooseMap;
        chooseMap = null;
        graph.game();
        return m;
    }

    public void setChooseMap(String chooseMap){
        this.chooseMap=chooseMap;
        synchronized (syncmap) {
            syncmap.notifyAll();
        }
    }

    @Override
    public boolean sendCards(String[]...  s) {
        for (int i = 0; i < s.length ; i++)
        {
            if (i == 0) {
                //System.out.println("Obbiettivi Pubblici: ");
                try {
                    graph.updatePublicTarget(s[i]);
                }catch (Exception e1){
                    graph.fatalDisconnection("Errore interno");
                }
            }
            else if (i == 1) {
                //System.out.println("Strumenti: ");
                try {
                    graph.updateTools(s[i]);
                }catch (Exception e2){
                    graph.fatalDisconnection("Errore interno");
                }
            }
            else if (i == 2)
            {
                //System.out.println("Obbiettivi Privati: ");
                try {
                    graph.updatePrivateTarget(s[i]);
                }catch (Exception e3){
                    graph.fatalDisconnection("Errore interno");
                }
            }
            //for (int j = 0; j< s[i].length ; j++)
            //    System.out.println(s[i][j]);

        }
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Update graphic turn">
    @Override
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException, RemoteException {
        setServerAlive(true);
        graph.updateDadiera(dadiera);

        return "ok";
    }

    @Override
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException, RemoteException {
        setServerAlive(true);
        graph.updateWindow(grid);
        return "ok";
    }

    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException {
        setServerAlive(true);
        graph.updateTokens(n);
        return "ok";
    }

    @Override
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException {
        setServerAlive(true);
        graph.updateRoundTrace(dice);
        return "ok";
    }

    @Override
    public void sendResults(String [] user, int [] point) throws RemoteException {
        setServerAlive(true);
        System.out.println("End game results:");
        for (int i = 0 ; i < user.length ; i++)
            System.out.println("Utente: " + user[i] + "\t Punti totalizzati: " + point[i]);
        graph.endGame(user, point);
    }

    @Override
    public String updateOpponents (String user, Pair[][] grids,boolean active)
    {
        setServerAlive(true);
        if (!user.equals(clientName))
        {
            //System.out.println(user + " " + active);
            graph.updateOpponents(grids, user,active);
        }
        return "ok";
    }
    //</editor-fold>

    //<editor-fold desc="Turn communication">
    public String doTurn ()
    {
        setServerAlive(true);
        MoveAction.clearMove();
        ToolAction.clearTool();
        graph.updateMessage("My turn");
        startTimerTurn();
        try {
            graph.setEnableBoard(true);
        }catch(Exception e){
            stopTimerTurn();
            e.printStackTrace();
        }
        //inTurn = true;
        setInTurn(true);
        return "ok";
    }

    public synchronized void myMove()
    {

        if (MoveAction.canMove())
            finishedMove = true;
        else
            finishedMove = false;

        if (finishedMove)
        {
            String msg = "";
            try {
                msg = MoveAction.perfromMove(server);
            } catch (RemoteException e) {
                e.printStackTrace();
                closeCommunication("Impossibile contattare il server");
                return;
            }
            graph.updateMessage(msg);

        }

    }

    public synchronized void pass() {
        try {
            if(timerTurn != null && timerTurn.isAlive()) {
                String s = server.passTurn();
                stopTimerTurn();
                graph.setEnableBoard(false);
                graph.setToolPhase(false);
                ToolAction.clearTool();
                MoveAction.clearMove();

                //inTurn = false;
                setInTurn(false);

                graph.updateMessage(s);
            }
        } catch (RemoteException e) {
            //e.printStackTrace();
        }
    }

    public synchronized void disconnect ()
    {
        if (isInTurn())
        {
            try {
                if(connectionStatusRMITimer != null) {
                    connectionStatusRMITimer.cancel();
                    connectionStatusRMITimer.purge();
                }
                server.disconnection();
            } catch (RemoteException e) {
                closeCommunication("Impossibile contattare il server");
            }
        }

    }
    //</editor-fold>

    //<editor-fold desc="Tool communication">
    public boolean toolPermission(int toolID) {
        String response;
        try {
            response = ToolAction.askToolPermission(server,toolID);
            System.out.println("tool response: " + response);
        } catch (RemoteException e) {
            e.printStackTrace();
            closeCommunication("Impossibile contattare il server");
            return false;
        }
        return response.equals("Richiesta utilizzo tool accolta");
    }

    public synchronized void useTool() {
        String msg = "";
        try {
            msg = ToolAction.performTool(server);
        } catch (RemoteException e) {
            //e.printStackTrace();
            closeCommunication("Impossibile contattare il server");
            return;
        }
        graph.updateMessage(msg);
        if (!"redyellowgreenbluepurple".contains(msg)) {
            graph.setToolPhase(false);
            ToolAction.clearTool();
        } else {
            ColorEnum[] color = Stream.of(ColorEnum.values()).toArray(ColorEnum[]::new);
            for(ColorEnum c : color) {
                if(c.toString().toLowerCase().equals(msg.toLowerCase()))
                    ToolAction.setDadieraPair(new Pair(c));
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    public boolean isConnected() {
        return connected;
    }

    private static void connection_parameters_setup() throws ParserXMLException{

        if(HOSTNAME == null)
            HOSTNAME = ParserXML.SetupParserXML.getHostName(FileLocator.getClientSettingsPath());

        //rmi setup
        RMI_REGISTRY_PORT = ParserXML.SetupParserXML.getRmiPort(FileLocator.getClientSettingsPath());
        SOCKET_PORT = ParserXML.SetupParserXML.getSocketPort(FileLocator.getClientSettingsPath());
        TURN_TIMEOUT = ParserXML.SetupParserXML.getTurnTimeLaps(FileLocator.getGameSettingsPath());
        STATUS_TIMEOUT = ParserXML.SetupParserXML.getStatusTimeLaps(FileLocator.getGameSettingsPath());
    }

    /**
     * @return if we are alive
     */
    public boolean ping ()
    {
        setServerAlive(true);
        return true;
    }

    public boolean sendMessage (String s)
    {
        setServerAlive(true);
        System.out.println(s);
        return true;
    }

    public boolean closeCommunication (String cause)
    {
        if(connectionStatusRMITimer != null) {
            connectionStatusRMITimer.cancel();
            connectionStatusRMITimer.purge();
        }
        graph.disconnection(cause);
        graph = null;
        return true;
    }

    @Override
    public void reconnect() throws RemoteException {
        graph.game();
    }

    @Override
    public void setAdapter(ServerModelAdapter sma) throws RemoteException {
        //unused
    }

    @Override
    public void setMatchHandler(MatchHandler match) throws RemoteException {
        //unused
    }

    private boolean isInTurn() {
        synchronized (lockInTurn) {
            return inTurn;
        }
    }

    private void setInTurn(boolean inTurn) {
        synchronized (lockInTurn) {
            this.inTurn = inTurn;
        }
    }

    private /*synchronized*/ boolean isServerAlive (){ return serverAlive;}

    private /*synchronized*/ void setServerAlive (boolean b){ serverAlive = b;}

    @Override
    public String getName() throws RemoteException {
        return clientName;
    }
    //</editor-fold>

    //<editor-fold desc="Timer turn">
    private void startTimerTurn() {
        timerTurn = new Thread(new GameTimer(TURN_TIMEOUT));
        timerTurn.start();
    }

    private void stopTimerTurn(){
        timerTurn.interrupt();
        timerTurn = null;
    }

    //use to time the setup and turn phase
    private class GameTimer implements Runnable {
        int time;

        public GameTimer(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                return;
            }
            closeCommunication("timeout reached");
        }
    }
    //</editor-fold>


    @Override
    public void close() throws RemoteException {
        //unused
    }
}
