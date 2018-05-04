package Server;

import Utilities.JSONFacilities;
import Utilities.LogFile;
import Exceptions.ClientOutOfReachException;

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

    public ServerConnectionHandler() throws ClientOutOfReachException
    {
        try {
            init_connection();
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
        } catch (IOException e)
        {
            try {
                client.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            throw new ClientOutOfReachException("Impossible to create Socket Buffer");
        }

    }

    private void init_connection() throws ClientOutOfReachException
    {
        serverSocket=null;
        try {
            initializer();
            serverSocket = new ServerSocket(PORT);
            LogFile.addLog("\nServer waiting for client on port " +  serverSocket.getLocalPort());

            // server infinite loop
            client = serverSocket.accept();
        }
        catch(Exception e)
        {
            try {
                serverSocket.close();
            } catch(Exception ex) { }
            //throw new ClientOutOfReachException("Impossible to accept client connection \n\r" + e.getStackTrace().toString());
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String login() throws ClientOutOfReachException
    {
        String user = "";
        try {
            outSocket.println("login");
            outSocket.println("Inserisci username");
            user = inSocket.readLine();
        } catch (IOException e) {
            throw new ClientOutOfReachException("Client is out of reach");
        }
        return user;
    }

    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        String response = "";

        try {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2); //creo il json da inviare
            outSocket.println("windowinit"); //dico al client che azione voglio eseguire
            outSocket.println("Scegli la vetrata");
            outSocket.println(jsonArray.toString());    //invio le due coppie di vetrate
            response = inSocket.readLine();  //mi aspetto il nome della vetrata scelta
        }
        catch (IOException e) {
            throw new ClientOutOfReachException("Client is out of reach");
        }
        catch (JSONException je) {
            throw new ClientOutOfReachException("JSON can't decrypt client message");
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
