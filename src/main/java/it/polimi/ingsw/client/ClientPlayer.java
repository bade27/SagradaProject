package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.*;
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
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientPlayer extends UnicastRemoteObject implements ClientRemoteInterface
{
    private static final String settings = "resources/client_settings.xml";

    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static int RMI_STUB_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private static boolean initialized = false;
    private int typeOfCOnnection; //1 rmi , 0 Socket

    private GUI graph;
    //private ClientModelAdapter adp;
    private ServerRemoteInterface server;

    //buffer mossa in upload
    private Move move;
    private ToolMove tmove;
    private boolean finishedMove = false;
    private int num_of_moves = 0;


    //<editor-fold desc="Initialization Phase">
    private static void connection_parameters_setup() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(settings);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        HOSTNAME = document.getElementsByTagName("hostName").item(0).getTextContent();

        //rmi setup
        RMI_REGISTRY_PORT = Integer.parseInt(document.getElementsByTagName("registryPort").item(0).getTextContent());
        RMI_STUB_PORT = Integer.parseInt(document.getElementsByTagName("stubPort").item(0).getTextContent());

        //socket setup
        SOCKET_PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
    }


    public ClientPlayer (int t,GUI g) throws RemoteException
    {
        typeOfCOnnection = t;
        this.graph = g;
        //adp = new ClientModelAdapter(graph);
        try
        {
            //since the parameters are static, the initialization is performed once
            if(!initialized) {
                connection_parameters_setup();
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

                /*for (int i = 0; i < e.length ; i++)
                    System.out.println(e[i]);*/

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
            System.out.println("Impossible to connect to Host with RMI");
            return;
        }
        catch (Exception e){
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
        System.out.println("Insert Username: ");
        Scanner cli = new Scanner(System.in);
        return cli.nextLine();
    }

    /**
     * Choosing window method
     */
    public String chooseWindow(ArrayList<String[]> list)
    {
        String choice = "";
        try {
            TimeUnit.SECONDS.sleep(3);//why?
            choice = chooseWindow(list.get(0),list.get(1));
        }
        catch (ClientOutOfReachException ex){
            return "";
        } catch (InterruptedException e) {
            System.out.println("test sleep interrupted");
            //e.printStackTrace();
        }
        return choice;
    }

    /**
     * Real Choosing window method
     */
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        //Ora qui ci deve essere la scelta dell'utente della carta
        System.out.println("Window selected:" + s1[0]);
        return s1[0];
    }

    @Override
    public boolean sendCards(String[]... s) throws RemoteException {
        for (int i = 0; i < s.length ; i++)
        {
            if (i == 0)
                System.out.println("Obbiettivi Pubblici: ");
            else
                System.out.println("Strumenti: ");
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

    @Override
    public void updateOpponents (String user, Pair[][] grids) {
        /*for(int k = 0; k < grids.length; k++){
            System.out.println("opponent " + (k + 1) );
            Pair[][] p = grids[k];*/
        System.out.println("Opponent: " + user);
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++)
                System.out.print(grids[i][j].toString() + "\t|\t");
            System.out.println();
        }
        //}
    }
    //</editor-fold>

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


    public String doTurn ()
    {
        num_of_moves = 0;
        clearMove();
        clearTool();
        graph.updateMessage("My turn");
        graph.setEnableBoard(true);
        return "ok";
    }

    public synchronized void myMove() {

        if (num_of_moves == 0)
        {
            if (move.getP() != null && move.getI() != null && move.getJ() != null)
                finishedMove = true;
            else
                finishedMove = false;

            if (finishedMove)
            {
                try {
                    String msg = server.makeMove(move);
                    graph.updateMessage(msg);
                    //graph.setEnableBoard(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    if (finishedMove) {
                        try {
                            String msg = server.makeMove(move);
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

    public synchronized void clearMove () {
        move = new Move();
    }

    public synchronized void setMovePair(Pair p) {
        this.move.setP(p);
    }

    public synchronized void setMoveIJ(int i, int j) {
        this.move.setIJ(i, j);
    }

    public synchronized void pass() {
        try {
            String s = server.passTurn();
            graph.setEnableBoard(false);
            graph.updateMessage(s);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean toolPermission(int toolID) {
        boolean response = false;
        try {
            response = server.askToolPermission();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return response;
    }

    public synchronized void useTool() {
        try {
            if(tmove.getId() != 0) {
                if(server.useTool(tmove)) {
                    System.out.println("hi");
                    graph.setToolPhase(false);
                    clearTool();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void clearTool() {
        tmove = new ToolMove();
    }

    public synchronized void setToolMovePair(Pair p) {
        this.tmove.setP(p);
    }

    public synchronized void setToolMoveIJ(int i, int j) {
        this.tmove.setIJ(i, j);
    }

    public synchronized void setToolInstruction(String instruction) {
        this.tmove.setInstruction(instruction);
    }

    public synchronized void setToolMoveID(int id) {
        tmove.setId(id);
    }
}
