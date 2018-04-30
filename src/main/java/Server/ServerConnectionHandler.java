package Server;

import Client.ClientConnectionHandler;
import Client.Graphic;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler {

    //contiene informazioni su indirizzo e porta del server
    private static final String settings = "resources/settings.xml";

    private static int PORT;

    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerSocket serverSocket;

    private static void initializer() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(settings);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
    }

    public ServerConnectionHandler() {
        try {
            init_connection();
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
        } catch (IOException e) {
            try {
                client.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void init_connection() {
        serverSocket=null;
        try {
            initializer();
            serverSocket = new ServerSocket(PORT);
            System.out.println("\nServer waiting for client on port " +  serverSocket.getLocalPort());

            // server infinite loop
            client = serverSocket.accept();
        }
        catch(Exception e) {
            System.out.println(e);
            try {
                serverSocket.close();
            } catch(Exception ex) {

            }
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String login() {
        String user = "";
        try {
            outSocket.println("login");
            outSocket.println("Inserisci username");
            user = inSocket.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String chooseWindow(String[] s1, String[] s2) {

        String response = "";

        try {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2); //creo il json da inviare
            outSocket.println("windowinit"); //dico al client che azione voglio eseguire
            outSocket.println("Scegli la vetrata");
            outSocket.println(jsonArray.toString());    //invio le due coppie di vetrate
            response = inSocket.readLine();  //mi aspetto il nome della vetrata scelta
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return response;

    }

    public boolean sendPrivateObjective(String privObj) {
        //mi aspetto un ok come risposta
        try {
            outSocket.println("privobj"); //dico al client che azione voglio eseguire
            outSocket.println(privObj);
            if(inSocket.readLine().equals("ok"))
                return true;//mi aspetto il nome della vetrata scelta
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendCards(String[]... s) {
        //mi aspetto un ok come risposta
    }

    public void moves() {
        //dico al client che è il suo turno, poi aspetto che invii la mossa fatta
    }

    public void close() {
        try {
            client.close();
        } catch(Exception e) {
            System.out.println("Exception: e="+e);
            e.printStackTrace();

            try {
                client.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
