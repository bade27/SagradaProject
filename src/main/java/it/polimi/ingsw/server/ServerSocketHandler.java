package it.polimi.ingsw.server;

import com.sun.corba.se.spi.activation.Server;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
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
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ServerSocketHandler implements ClientRemoteInterface, Runnable
{
    private int PORT;
    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerSocket serverSocket;


    private String action;
    private final Integer syncronator = 5;

    private boolean isAlive = true;
    private boolean isConnected;
    private MatchHandler match;
    private ServerModelAdapter adapter;

    private Thread deamon;

    public ServerSocketHandler(int port)
    {
        PORT = port;
        isConnected = false;

    }

    @Override
    public void run()
    {
        try
        {
            action = inSocket.readLine();
            switch (action)
            {
                case "move":
                    System.out.println("move entered");
                    String objs = inSocket.readLine();
                    receiveMove(objs);
                default:
                    System.out.println("Problem");
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

        try
        {
            boolean reachable = ping();
            if (reachable)
            {
                outSocket.write("login\n");
                outSocket.flush();
                outSocket.write("Inserisci username\n");
                outSocket.flush();
                user = inSocket.readLine();
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (Exception e)
        {
            throw new ClientOutOfReachException();
        }

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

                response = inSocket.readLine();
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        } catch (IOException e)
        {
            e.printStackTrace();
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
                //response = waitResponse();
                response = inSocket.readLine();
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (JSONException je)
        {
            LogFile.addLog("JSON can't encrypt client message", je.getStackTrace());
            throw new ClientOutOfReachException();
        } catch (IOException e)
        {
            e.printStackTrace();
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
            //String r = waitResponse();
            //System.out.println("ping recive:" + r);
            reply = r.equals("pong");
        } catch (IOException ste)
        {
            ste.printStackTrace();
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

    public void setMatch(MatchHandler match)
    {
        this.match = match;
    }

    //</editor-fold>

    //<editor-fold desc="Update clients">

    /**
     * Update client structure for all type
     *
     * @param json json object to send
     * @param msg  msg to send
     * @return return value from client
     */
    private String updateClient(String json, String msg) throws ClientOutOfReachException
    {
        String response = "";

        try
        {
            boolean reachable = ping();
            if (reachable)
            {
                outSocket.write(msg);
                outSocket.flush();
                StringBuilder windows = new StringBuilder(json);
                windows.append("\n");
                outSocket.write(windows.toString());
                outSocket.flush();
                response = inSocket.readLine();
                //response = waitResponse();
                System.out.println("update: " + msg + " response: " + response);
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new ClientOutOfReachException();
        }

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
    public String updateOpponents(String user, Pair[][] grid) throws ClientOutOfReachException, RemoteException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeMatrixPair(user, grid);
            return updateClient(jsonArray.toString(), "up_opponents\n");
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
        try
        {
            boolean reachable = ping();
            if (reachable)
            {
                outSocket.write("doTurn\n");
                outSocket.flush();
                response = inSocket.readLine();
                System.out.println("doTurn Response: " + response);
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();

            startDaemon();

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new ClientOutOfReachException();
        }
        return response;
    }

    /**
     * Recive move from client and process it
     *
     * @param message return message to client
     */
    private void receiveMove(String message)
    {
        ArrayList arr = JSONFacilities.decodeMove(message);

        Coordinates coord = new Coordinates((Integer) arr.get(0), (Integer) arr.get(1));
        Pair pair = new Pair((Integer) arr.get(2), (ColorEnum) arr.get(3));

        String response = "Impossibile eseguire la mossa";
        if (!adapter.CanMove())
            response = "Hai già mosso in questo turno";
        else
        {
            try
            {
                adapter.addDiceToBoard(coord.getI(), coord.getJ(), new Dice(pair.getValue(), pair.getColor()));

                response = "Mossa applicata correttamente";
            } catch (ModelException e)
            {
                response = e.getMessage();
            }
        }
        try
        {
            outSocket.write(response + "\n");
            outSocket.flush();

            match.updateClient();

            startDaemon();

        } catch (Exception e)
        {
            e.printStackTrace();
            LogFile.addLog("Impossible to notify move");
        }

    }
    //</editor-fold>

    //<editor-fold desc="End Game Phase">
    @Override
    public void sendResults(String[] user, int[] point) throws RemoteException, ClientOutOfReachException
    {
        try
        {
            JSONArray jsonArray = JSONFacilities.encodeStringInteger(user, point);
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



    private void startDaemon ()
    {
        try
        {
            //Test
            deamon = new Thread(this);
            deamon.start();
            //Test
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setAdapter(ServerModelAdapter adapter)
    {
        this.adapter = adapter;
    }


}
