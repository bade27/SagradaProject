package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
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

public class ClientPlayer extends UnicastRemoteObject implements ClientRemoteInterface
{
    private static final String settings = "resources/client_settings.xml";

    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static int RMI_STUB_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private static int INIT_EXECUTE_TIME;
    private static int MOVE_EXECUTE_TIME;
    private static boolean initialized = false;


    private Graphic graph;
    ClientModelAdapter adp;

    int typeOfCOnnection; //1 rmi , 0 Socket

    private ServerRemoteInterface server;

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
        INIT_EXECUTE_TIME = Integer.parseInt(document.getElementsByTagName("init").item(0).getTextContent());
        MOVE_EXECUTE_TIME = Integer.parseInt(document.getElementsByTagName("move").item(0).getTextContent());
    }


    public ClientPlayer (int t,Graphic g) throws RemoteException
    {
        typeOfCOnnection = t;
        this.graph = g;
        adp = new ClientModelAdapter(graph);
        try
        {
            //since the parameters are static, the initialization is performed once
            if(!initialized) {
                connection_parameters_setup();
                initialized = true;
            }

            //if connection is socket, creates socket connect
            if (typeOfCOnnection == 0)
                server = new ClientSocketHandler(this, HOSTNAME, SOCKET_PORT, INIT_EXECUTE_TIME, MOVE_EXECUTE_TIME);

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
        try
        {
            adp.initializeWindow(s1[0]);
        }
        catch (ModelException ex){
            return "";
        }
        return s1[0];
    }

    @Override
    public boolean sendCards(String[]... s) throws RemoteException {
        return true;

    }

    //</editor-fold>

    //<editor-fold desc="Utilities: Ping">
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

    public boolean closeComunication (String cause)
    {
        System.out.println("Game ended cause " + cause);
        return true;
        //Graphic.setpopup connection down
    }
    //</editor-fold>

}
