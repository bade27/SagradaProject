package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.JSONFacilities;
import it.polimi.ingsw.utilities.LogFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerSocketHandler implements ClientRemoteInterface,Runnable
{
    private ServerModelAdapter adapter;

    private int PORT;
    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerSocket serverSocket;

    private boolean isAlive = true;
    private boolean isConnected;

    private Thread deamon;

    public ServerSocketHandler(int port)
    {
        PORT = port;
        isConnected = false;

    }

    @Override
    public void run()
    {
        boolean stop = false;
        String action = "";
        try
        {
            while ((action = inSocket.readLine()) != "close" && action != null)
            {
                switch (action)
                {
                    case "move":
                        System.out.println("mossssa?");
                        continue;

                    case "content":
                        continue;
                    default:
                        //System.out.println(action);
                        continue;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    //<editor-fold desc="Connection Phase">

    /**
     * sets up connection and in and out buffers
     *
     * @throws ClientOutOfReachException if client not reachable
     */
    public void createConnection() throws ClientOutOfReachException
    {
        try
        {
            init_connection();
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
            isConnected = true;
        } catch (IOException | NullPointerException e)
        {
            try
            {
                serverSocket.close();
                client.close();
            } catch (Exception ex)
            {
                //ex.printStackTrace();
            }
        }
    }

    /**
     * sets up the connection with the player
     *
     * @throws ClientOutOfReachException if client could not connect to the server
     */
    private void init_connection() throws ClientOutOfReachException
    {
        serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(PORT);
            LogFile.addLog("\nit.polimi.ingsw.server socket waiting for client on port " + serverSocket.getLocalPort());

            client = serverSocket.accept();
        } catch (Exception e)
        {
            //e.printStackTrace();
            try
            {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException npe) {}
            //throw new ClientOutOfReachException("Impossible to accept client connection");
        } finally
        {
            try
            {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException npe)
            {
                npe.printStackTrace();
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">

    /**
     * login function
     *
     * @return weather the client has successfully logged or not
     * @throws ClientOutOfReachException if client has disconnected
     */
    public String login() throws ClientOutOfReachException
    {
        String user = "";

        boolean reachable = ping();
        if (reachable)
        {
            outSocket.write("login\n");
            outSocket.flush();
            outSocket.write("Inserisci username\n");
            outSocket.flush();
            try
            {
                user = inSocket.readLine();
                isAlive = true;
            } catch (IOException e)
            {
                isAlive = false;
            }
        } else
            isAlive = false;

        if (!isAlive)
            throw new ClientOutOfReachException();
        return user;

    }

    /**
     * let the player decide which window to use
     *
     * @param s1 firs pair of windows
     * @param s2 second pair of windows
     * @return the window chosen by the player
     * @throws ClientOutOfReachException
     */
    public String chooseWindow(String[] s1, String[] s2) throws ClientOutOfReachException
    {
        String response = "";

        try
        {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2);
            boolean reachable = ping();
            if (reachable)
            {
                outSocket.write("windowinit\n");
                outSocket.flush();
                StringBuilder windows = new StringBuilder(jsonArray.toString());
                windows.append("\n");
                outSocket.write(windows.toString());
                outSocket.flush();
                try
                {
                    response = inSocket.readLine();
                    isAlive = true;
                } catch (IOException ste)
                {
                    isAlive = false;
                }
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
            outSocket.write("ok\n");
            outSocket.flush();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }

        return response;

    }

    /**
     * sends the cards (public objectives and tools) to the player
     *
     * @param s array of strings representing the list of public objectives and tools in the current game
     * @throws ClientOutOfReachException if the client is disconnected
     */
    public boolean sendCards(String[]... s) throws ClientOutOfReachException
    {
        String response = "";

        try
        {
            boolean reachable = ping();
            if (reachable)
            {
                String msgs = "cards\n";
                JSONArray jsonArray = JSONFacilities.encodeStringArrays(s[0], s[1], s[2]);
                outSocket.write(msgs);
                outSocket.flush();
                StringBuilder cards = new StringBuilder();
                cards.append(jsonArray.toString());
                cards.append("\n");
                outSocket.write(cards.toString());
                outSocket.flush();
                try
                {
                    response = inSocket.readLine();
                    isAlive = true;
                } catch (IOException ste)
                {
                    isAlive = false;
                }
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
        return isAlive;
    }
    //</editor-fold>

    //<editor-fold desc="Utilitites">

    /**
     * ping function to see if the client is reachable
     *
     * @return weather the client is reachable or not
     */
    public boolean ping()
    {
        //ping-pong communication
        boolean reply = false;
        try
        {
            outSocket.write("ping\n");
            outSocket.flush();
            String r = inSocket.readLine();
            reply = r.equals("pong");
        } catch (IOException ste)
        {
            return false;
        }
        return reply;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public boolean sendMessage(String s) throws ClientOutOfReachException
    {
        outSocket.write("msg\n");
        if (outSocket.checkError())
            isAlive = false;
        StringBuilder msg = new StringBuilder(s);
        msg.append("\n");
        outSocket.write(msg.toString());
        if (outSocket.checkError())
            isAlive = false;

        if (!isAlive)
            throw new ClientOutOfReachException();
        return true;
    }

    public boolean closeCommunication(String cause) throws ClientOutOfReachException
    {
        StringBuilder sb = new StringBuilder(cause);
        outSocket.write("close\n");
        outSocket.flush();
        sb.append("\n");
        outSocket.write(sb.toString());
        return outSocket.checkError();
    }


    //</editor-fold>

    //<editor-fold desc="Update clients">

    /**
     * Update client structure for all type
     *
     * @param json json array to send
     * @param msg  msg to send
     * @return return value from client
     */
    private String updateClient(String json, String msg) throws ClientOutOfReachException
    {
        String response = "";

        boolean reachable = ping();
        if (reachable)
        {
            outSocket.write(msg);
            outSocket.flush();
            StringBuilder windows = new StringBuilder(json);
            windows.append("\n");
            outSocket.write(windows.toString());
            outSocket.flush();
            try
            {
                response = inSocket.readLine();
                isAlive = true;
            } catch (IOException ste)
            {
                isAlive = false;
            }
        } else
            isAlive = false;

        if (!isAlive)
            throw new ClientOutOfReachException();


        return response;
    }


    @Override
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException, RemoteException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeArrayPair(dadiera);
            return updateClient(jsonArray.toString(), "up_dadiera\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    @Override
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException, RemoteException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeMatrixPair(grid);
            return updateClient(jsonArray.toString(), "up_window\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    @Override
    public void updateOpponents(String user, Pair[][] grid) throws ClientOutOfReachException, RemoteException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeMatrixPair(user, grid);
            updateClient(jsonArray.toString(), "up_opponents\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    @Override
    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException
    {
        try
        {
            JSONObject jsonObj = JSONFacilities.encodeInteger(n);
            return updateClient(jsonObj.toString(), "up_tokens\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    @Override
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException, ClientOutOfReachException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeMatrixPair(dice);
            return updateClient(jsonArray.toString(), "up_trace\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Turn Phase">
    /**
     * Send message to notify client about his turn
     */
    @Override
    public String doTurn() throws ClientOutOfReachException
    {
        String response = "";
        boolean reachable = ping();
        if (reachable)
        {
            outSocket.write("doTurn\n");
            outSocket.flush();
            try
            {
                response = inSocket.readLine();
                isAlive = true;
            } catch (IOException ste)
            {
                isAlive = false;
            }
        } else
            isAlive = false;

        if (!isAlive)
            throw new ClientOutOfReachException();

        //Test
        deamon = new Thread(this);
        deamon.start();
        //Test

        return response;
    }
    //</editor-fold>

    //<editor-fold desc="End Game Phase">
    @Override
    public void sendResults(String[] user, int[] point) throws RemoteException,ClientOutOfReachException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeStringInteger(user,point);
            updateClient(jsonArray.toString(), "up_results\n");
        } catch (ClientOutOfReachException e)
        {
            throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    /*
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
    //</editor-fold>


}
