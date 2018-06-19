package it.polimi.ingsw.client;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.utilities.FileLocator;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class ClientPlayer extends UnicastRemoteObject implements ClientRemoteInterface
{
    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static int RMI_STUB_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private static boolean initialized = false;
    private int typeOfCOnnection; //1 rmi , 0 Socket
    private UI graph;
    private ServerRemoteInterface server;
    private String clientName;
    private String chooseMap;
    //buffer mossa in upload
    private boolean finishedMove = false;
    private int num_of_moves = 0;
    private final Integer synclogin=5;
    private final Integer syncmap = 5;

    //<editor-fold desc="Initialization Phase">
    private static void connection_parameters_setup() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(FileLocator.getClientSettingsPath());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        if(HOSTNAME == null)
            HOSTNAME = document.getElementsByTagName("hostName").item(0).getTextContent();

        //rmi setup
        RMI_REGISTRY_PORT = Integer.parseInt(document.getElementsByTagName("registryPort").item(0).getTextContent());
        RMI_STUB_PORT = Integer.parseInt(document.getElementsByTagName("stubPort").item(0).getTextContent());

        //socket setup
        SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
    }


    public ClientPlayer (int t, UI g, String serverIP) throws RemoteException
    {
        if(!serverIP.isEmpty())
            HOSTNAME = serverIP;
        typeOfCOnnection = t;
        this.graph = g;
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
            return;
        }
        catch (RemoteException e){
            //e.printStackTrace();
            System.out.println("Impossible to connect to Host with RMI");
            return;
        }
        catch (Exception e){
            //e.printStackTrace();
            System.out.println("Impossible to connect to Host");
            return;
        }
        System.out.println("Client connected");
    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">
    /**
     * Request to user a username
     * @return username inserted
     */
    public String login() throws ClientOutOfReachException
    {
        if (clientName == null)
        {
            try {
                graph.login("nome già esistente");
            }
            catch (Exception e){
                e.printStackTrace();
            }

            try {
                synchronized (synclogin)
                {
                    while (clientName == null)
                        synclogin.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String ret = clientName;
        clientName = null;
        return ret;
    }

    /**
     * Set Client name
     * @param clientName
     */
    public void setClientName(String clientName)
    {
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
        String choice = "";
        try {
            choice = chooseWindow(list.get(0),list.get(1));
        }
        catch (ClientOutOfReachException ex){
            return "";
        }
        return choice;
    }

    /**
     * Real Choosing window method
     */
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        if (chooseMap == null)
        {
            try {
                graph.maps(s1, s2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                synchronized (syncmap) {
                    while (chooseMap == null)
                        syncmap.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String m = chooseMap;
        chooseMap = null;
        //String m = s1[0];

        Boolean b = true;
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
    public boolean sendCards(String[]...  s) throws RemoteException {
        for (int i = 0; i < s.length ; i++)
        {
            if (i == 0) {
                System.out.println("Obbiettivi Pubblici: ");
                try {
                    graph.updatePublicTarget(s[i]);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
            else if (i == 1) {
                System.out.println("Strumenti: ");
                try {
                    graph.updateTools(s[i]);
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
            else if (i == 2)
            {
                System.out.println("Obbiettivi Privati: ");
                try {
                    graph.updatePrivateTarget(s[i]);
                }catch (Exception e3){
                    e3.printStackTrace();
                }
            }
            for (int j = 0; j< s[i].length ; j++)
                System.out.println(s[i][j]);

        }
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Update graphic turn">
    @Override
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException, RemoteException {
        graph.updateDadiera(dadiera);
        return "ok";
    }

    @Override
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException, RemoteException {
        graph.updateWindow(grid);
        return "ok";
    }

    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException {
        graph.updateTokens(n);
        return "ok";
    }

    @Override
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException {
        graph.updateRoundTrace(dice);
        return "ok";
    }

    @Override
    public void sendResults(String [] user, int [] point) throws RemoteException {
        System.out.println("End game results:");
        for (int i = 0 ; i < user.length ; i++)
            System.out.println("Utente: " + user[i] + "\t Punti totalizzati: " + point[i]);
        graph.endGame(user, point);
    }

    @Override
    public String updateOpponents (String user, Pair[][] grids) {
        graph.updateOpponents(grids, user);
        return "ok";
    }
    //</editor-fold>

    //<editor-fold desc="Turn communication">
    public String doTurn ()
    {
        num_of_moves = 0;
        MoveAction.clearMove();
        ToolAction.clearTool();
        graph.updateMessage("My turn");
        graph.setEnableBoard(true);
        return "ok";
    }

    public synchronized void myMove() {

        if (num_of_moves == 0)
        {
            if (MoveAction.canMove())
                finishedMove = true;
            else
                finishedMove = false;

            if (finishedMove)
            {
                try {
                    String msg = MoveAction.perfromMove(server);
                    graph.updateMessage(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    if (finishedMove) {
                        try {
                            String msg = MoveAction.perfromMove(server);
                            graph.updateMessage(msg);
                            graph.setEnableBoard(false);
                            num_of_moves++;
                        } catch (RemoteException ex) {
                            e.printStackTrace();
                        }
                    }
                }

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
            return false;
        }
        return response.equals("Richiesta utilizzo tool accolta");
    }

    public synchronized void useTool() {
        String msg = ToolAction.performTool(server);
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

    public synchronized void pass() {
        try {
            String s = server.passTurn();
            graph.setEnableBoard(false);
            graph.updateMessage(s);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //<editor-fold desc="Utilities">
    /**
     * @return if we are alive
     */
    public boolean ping ()
    {
        return true;
    }

    public boolean sendMessage (String s)
    {
        System.out.println(s);
        return true;
    }

    public boolean closeCommunication (String cause)
    {
        System.out.println("Game ended because " + cause);
        return true;
        //Graphic.setpopup connection down

    }
    //</editor-fold>
}
