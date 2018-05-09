package it.polimi.ingsw.Server;

import it.polimi.ingsw.Utilities.JSONFacilities;
import it.polimi.ingsw.Utilities.LogFile;
import it.polimi.ingsw.Exceptions.ClientOutOfReachException;

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
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ServerConnectionHandler {

    //contiene informazioni su indirizzo e porta del server
    private static final String settings = "resources/settings.xml";

    private static int PORT;

    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerSocket serverSocket;

    private final int PING_TIMEOUT = 10000; //10 sec
    private final int ACTION_TIMEOUT = 10000; //5 min
    private boolean isAlive = true;

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

    private boolean ping() {
        //setting up ping timeout
        try {
            client.setSoTimeout(PING_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //ping-pong communication
        boolean reply = false;
        try {
            outSocket.write("ping\n");
            outSocket.flush();
            String r = inSocket.readLine();
            reply = r.equals("pong");
            System.out.println(r);
        } catch (SocketTimeoutException ste) {
            reply = false;
        } catch (IOException e) {
            e.getMessage();
        }
        return reply;

    }

    private void init_connection() throws ClientOutOfReachException
    {
        serverSocket=null;
        try {
            initializer();
            serverSocket = new ServerSocket(PORT);
            LogFile.addLog("\nit.polimi.ingsw.Server waiting for client on port " +  serverSocket.getLocalPort());

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
        /*try {
            outSocket.println("login");
            outSocket.println("Inserisci username");
            user = inSocket.readLine();
        } catch (IOException e) {
            throw new ClientOutOfReachException("it.polimi.ingsw.Client is out of reach");
        }
        return user;*/

        //non decommmentare: funziona, ma la parte corrispondente sul client è in costruzione (c'è la verisione vecchia)
        boolean reachable = ping();
        if(reachable) {
            //setting up messages timeout
            try {
                client.setSoTimeout(ACTION_TIMEOUT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            outSocket.write("login\n");
            outSocket.flush();
            outSocket.write("Inserisci username\n");
            outSocket.flush();
            try {
                user = inSocket.readLine();
                isAlive = true;
            } catch (SocketTimeoutException ste) {
                boolean alive = ping();
                if (!alive) {
                    isAlive = false;
                } else {
                    isAlive = true;
                    System.out.println("time's up");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else isAlive = false;

        if(!isAlive)
            throw new ClientOutOfReachException("it.polimi.ingsw.Client is out of reach");
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
            throw new ClientOutOfReachException("it.polimi.ingsw.Client is out of reach");
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
        outSocket.println("close");
        try {
            if (inSocket.readLine() == "ok") {
                try {
                    client.close();
                } catch (Exception e) {
                    System.out.println("Exception: e=" + e);
                    e.printStackTrace();

                    try {
                        client.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
