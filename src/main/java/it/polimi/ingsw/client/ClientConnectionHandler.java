package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.utilities.JSONFacilities;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class ClientConnectionHandler implements Runnable {

    //contiene informazioni su indirizzo e porta del server
    private static final String settings = "resources/settings.xml";

    private static String address;
    private static int PORT;
    private static int INIT_EXECUTE_TIME;
    private static int MOVE_EXECUTE_TIME;
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
        INIT_EXECUTE_TIME = Integer.parseInt(document.getElementsByTagName("init").item(0).getTextContent());
        MOVE_EXECUTE_TIME = Integer.parseInt(document.getElementsByTagName("move").item(0).getTextContent());
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
            int executionTime = 0;
            ExecutorService executor = Executors.newFixedThreadPool(1);
            while( (action = inSocket.readLine()) != "stop" )  {
                switch (action) {
                    case "windowinit":
                        executionTime = INIT_EXECUTE_TIME;
                        stopTask(() -> chooseWindow(), executionTime, executor);
                        continue;
                    case "privobj":
                        myPrivateObj();
                        continue;
                    case "login":
                        executionTime = INIT_EXECUTE_TIME;
                        stopTask(() -> login(), executionTime, executor);
                        continue;
                    case "close":
                        close();
                        continue;
                    case "ping":
                        outSocket.write("pong\n");
                        outSocket.flush();
                        continue;
                    case "wellcome!":
                        System.out.println(action);
                        continue;
                    default:
                        return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Boolean chooseWindow()
    {
        try {
            System.out.println(inSocket.readLine());
            String json = inSocket.readLine();
            StringBuilder choice = new StringBuilder(graph.chooseWindow(JSONFacilities.decodeStringArrays(json)));
            try {
                adp.initializeWindow(choice.toString());
            }catch (ModelException ex) {
                System.out.println(ex.getMessage());
            }
            choice.append("\n");
            outSocket.write(choice.toString());
            return outSocket.checkError();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return false;
    }

    public void myPrivateObj() {
        try {
            String response = graph.myPrivateObj(inSocket.readLine()).toLowerCase();
            outSocket.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean login() {
        try {
            //Da modificare con finestra a popup con username
            Scanner cli = new Scanner(System.in);
            System.out.println(inSocket.readLine());
            StringBuilder username = new StringBuilder(cli.nextLine());
            username.append("\n");
            outSocket.write(username.toString());
            return outSocket.checkError();
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
        return false;
    }

    private <T> void stopTask(Callable<T> task, int executionTime, ExecutorService executor) {
        Future future = executor.submit(task);
        try {
            future.get(executionTime, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            //System.out.println(te.getMessage());
            System.out.println("too late to reply");
        } catch (InterruptedException ie) {
            //System.out.println(ie.getMessage());
        } catch (ExecutionException ee) {
            //System.out.println(ee.getMessage());
        } finally {
            future.cancel(true);
        }
    }

    private void close() {
        outSocket.println("ok");
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
