package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.utilities.JSONFacilities;
import it.polimi.ingsw.utilities.LogFile;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketHandler extends Thread implements ClientRemoteInterface
{
    private LogFile log;

    private int PORT;

    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerSocket serverSocket;

    private boolean isAlive = true;
    private boolean isConnected;


    public ServerSocketHandler(LogFile l, int port) throws ClientOutOfReachException
    {
        PORT = port;
        isConnected = false;
        log = l;
    }

    /**
     *sets up connection and in and out buffers
     * @throws ClientOutOfReachException if client not reachable
     */
    public void createConnection () throws ClientOutOfReachException
    {
        try {
            init_connection();
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
            isConnected = true;
        }
        catch (IOException|NullPointerException e)
        {
            try {
                serverSocket.close();
                client.close();
            } catch(Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    /**
     * Used for close socket connection for RMI
     */
    public void run ()
    {
        try{
            serverSocket.close();
            client.close();
        }catch (Exception e){
            //log.addLog("Impossible to stop socket accept");
        }
    }

    /**
     * ping function to see if the client is reachable
     * @return weather the client is reachable or not
     */
    public boolean ping()
    {
        //ping-pong communication
        boolean reply = false;
        try {
            outSocket.write("ping\n");
            outSocket.flush();
            String r = inSocket.readLine();
            reply = r.equals("pong");
        } catch (SocketTimeoutException ste) {
            reply = false;
            log.addLog("Ping failed ", ste.getStackTrace());
        } catch (IOException e) {
            log.addLog("Ping failed ", e.getStackTrace());
            return false;
        }
        return reply;
    }

    /**
     * sets up the connection with the player
     * @throws ClientOutOfReachException if client could not connect to the server
     */
    private void init_connection() throws ClientOutOfReachException
    {
        serverSocket=null;
        try {
            serverSocket = new ServerSocket(PORT);
            log.addLog("\nit.polimi.ingsw.server socket waiting for client on port " +  serverSocket.getLocalPort());

            client = serverSocket.accept();
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch(IOException  npe) {}
            //throw new ClientOutOfReachException("Impossible to accept client connection");
        }
        finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException npe) {
                npe.printStackTrace();
            }
        }
    }

    /**
     * login function
     * @return weather the client has successfully logged or not
     * @throws ClientOutOfReachException if client has disconnected
     */
    public String login() throws ClientOutOfReachException
    {
        String user = "";

        boolean reachable = ping();
        if(reachable) {
            outSocket.write("login\n");
            outSocket.flush();
            outSocket.write("Inserisci username\n");
            outSocket.flush();
            try {
                user = inSocket.readLine();
                isAlive = true;
            } catch (IOException e) {
                isAlive = false;
            }
        } else isAlive = false;

        if(!isAlive)
            throw new ClientOutOfReachException("client is out of reach");
        return user;

    }

    /**
     * let the player decide which window to use
     * @param s1 firs pair of windows
     * @param s2 second pair of windows
     * @return the window chosen by the player
     * @throws ClientOutOfReachException
     */
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        String response = "";

        try {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2);
            boolean reachable = ping();
            if (reachable) {
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
                    isAlive = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException("client is out of reach");
            outSocket.write("ok\n");
            outSocket.flush();
        } catch (JSONException je) {
            throw new ClientOutOfReachException("JSON can't encrypt client message");
        }

        return response;

    }


    public boolean isConnected() {
        return isConnected;
    }

    /*
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
    }*/


    /**
     * sends the cards (public objectives and tools) to the player
     * @param s array of strings representing the list of public objectives and tools in the current game
     * @throws ClientOutOfReachException if the client is disconnected
     */
    public boolean sendCards(String[]... s) throws ClientOutOfReachException {
        String response = "";

        try {
            //System.out.println(s.length);
            boolean reachable = ping();
            if (reachable) {
                String[] msgs = {"pub_objs\n", "tools\n"};
                //System.out.println(s.length);
                for(int i = 0; i < s.length; i++) {
                    JSONArray jsonArray = JSONFacilities.encodeStringArrays(s[i]);
                    outSocket.write(msgs[i]);
                    outSocket.flush();
                    StringBuilder cards = new StringBuilder();
                    cards.append(jsonArray.toString());
                    cards.append("\n");
                    outSocket.write(cards.toString());
                    outSocket.flush();
                    try {
                        response = inSocket.readLine();
                        isAlive = true;
                    } catch (SocketTimeoutException ste) {
                        isAlive = false;
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException("client is out of reach");
        } catch (JSONException je) {
            throw new ClientOutOfReachException("JSON can't encrypt client message");
        }
        return isAlive;
    }

    public boolean sendMessage (String s) throws ClientOutOfReachException {
        StringBuilder msg = new StringBuilder(s);
        msg.append("\n");
        outSocket.write(msg.toString());
        if(outSocket.checkError())
            isAlive = false;

        if(!isAlive)
            throw new ClientOutOfReachException("Client is out of reach");
        return true;
    }

    public boolean closeCommunication ( String cause) throws ClientOutOfReachException
    {
        StringBuilder sb = new StringBuilder(cause);
        outSocket.write("close\n");
        outSocket.flush();
        sb.append("\n");
        outSocket.write(sb.toString());
        return outSocket.checkError();
    }
/*
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
    }*/
}
