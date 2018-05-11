package it.polimi.ingsw.server;

import it.polimi.ingsw.utilities.JSONFacilities;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;

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

    private static int PING_TIMEOUT; //10 sec
    private static int ACTION_TIMEOUT; //5 min
    private boolean isAlive = true;

    private static void initializer() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(settings);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        PORT = Integer.parseInt(document.getElementsByTagName("portNumber").item(0).getTextContent());
        PING_TIMEOUT = Integer.parseInt(document.getElementsByTagName("ping").item(0).getTextContent());
        ACTION_TIMEOUT = Integer.parseInt(document.getElementsByTagName("action").item(0).getTextContent());

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

    public boolean ping()
    {
        //setting up ping timeout
        try {
            client.setSoTimeout(PING_TIMEOUT);
        } catch (SocketException e) {
            LogFile.addLog("Ping Failed" , e.getStackTrace());
            return false;
        }

        //ping-pong communication
        boolean reply = false;
        try {
            outSocket.write("ping\n");
            outSocket.flush();
            String r = inSocket.readLine();
            reply = r.equals("pong");
        } catch (SocketTimeoutException ste) {
            reply = false;
            LogFile.addLog("Ping Failed" , ste.getStackTrace());
        } catch (IOException e) {
            LogFile.addLog("Ping Failed" , e.getStackTrace());
            return false;
        }
        return reply;
    }

    private void init_connection() throws ClientOutOfReachException
    {
        serverSocket=null;
        try {
            initializer();
            serverSocket = new ServerSocket(PORT);
            LogFile.addLog("\nit.polimi.ingsw.server waiting for client on port " +  serverSocket.getLocalPort());

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
                    //System.out.println("time's up");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else isAlive = false;

        if(!isAlive)
            throw new ClientOutOfReachException("it.polimi.ingsw.client is out of reach");
        return user;

    }

    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        String response = "";

        try {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2);
            boolean reachable = ping();
            if (reachable) {
                //setting up messages timeout
                try {
                    client.setSoTimeout(ACTION_TIMEOUT);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                outSocket.write("windowinit\n");
                outSocket.flush();
                outSocket.write("Scegli la vetrata\n");
                outSocket.flush();
                StringBuilder windows = new StringBuilder(jsonArray.toString());
                windows.append("\n");
                outSocket.write(windows.toString());
                outSocket.flush();
                try {
                    response = inSocket.readLine();
                    isAlive = true;
                } catch (SocketTimeoutException ste) {
                    boolean alive = ping();
                    if (!alive) {
                        isAlive = false;
                    } else {
                        isAlive = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException("it.polimi.ingsw.client is out of reach");
            outSocket.write("ok\n");
            outSocket.flush();
        } catch (JSONException je) {
            throw new ClientOutOfReachException("JSON can't encrypt client message");
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