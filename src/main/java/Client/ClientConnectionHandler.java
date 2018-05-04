package Client;

import Exceptions.ModelException;
import Utilities.JSONFacilities;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnectionHandler implements Runnable {

    //contiene informazioni su indirizzo e porta del server
    private static final String settings = "resources/settings.xml";

    private static String address;
    private static int PORT;
    private static boolean initialized = false;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    private Thread deamon;

    private Graphic graph;
    ClientModelAdapter adp;

    private static void initializer() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(settings);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        address = document.getElementsByTagName("hostName").item(0).getTextContent();
        PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
    }

    public ClientConnectionHandler(Graphic c) {
        this.graph = c;
        adp = new ClientModelAdapter(graph);
        System.out.println("connecting...");
        try{
            if(!initialized) {
                initializer();
                initialized = true;
            }
            socket = new Socket(address, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            System.out.println("initialization gone wrong");
            e.printStackTrace();
        }
        deamon = new Thread(this);
        deamon.start();
        System.out.println("connected");
    }

    @Override
    public void run() {

        try {
            String action = "";
            while( (action = inSocket.readLine()) != "stop" )  {
                switch (action) {
                    case "windowinit":
                        chooseWindow();
                        continue;
                    case "privobj":
                        myPrivateObj();
                        continue;
                    case "login":
                        login();
                        continue;
                    default:
                        return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void chooseWindow()
    {
        try {
            System.out.println(inSocket.readLine());
            String json = inSocket.readLine();
            String choice = graph.chooseWindow(JSONFacilities.decodeStringArrays(json));
            try {
                adp.initializeWindow(choice);
            }catch (ModelException ex) {
                System.out.println(ex.getMessage());
            }
            outSocket.println(choice);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public void myPrivateObj() {
        try {
            String response = graph.myPrivateObj(inSocket.readLine()).toLowerCase();
            outSocket.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        try {
            //Da modificare con finestra a popup con username
            Scanner cli = new Scanner(System.in);
            System.out.println(inSocket.readLine());
            String username = cli.nextLine();
            outSocket.println(username);

            //SOLO PER TEST
            /*if (username.equals("B"))
            {
                socket.close();
                outSocket.close();
                inSocket.close();
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            socket.close();
        } catch(Exception e) {
            System.out.println("Exception: "+e);
            e.printStackTrace();
        } finally {
            // Always close it:
            try {
                socket.close();
            } catch(IOException ex) {
                System.err.println("Socket not closed");
            }
        }
    }
}
