package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.JSONFacilities;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.Wrapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

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
                    //System.out.println("move entered");
                    String mv = inSocket.readLine();
                    receiveMove(mv);
                    break;
                case "pass_turn":
                    //System.out.println("pass entered");
                    passTurn();
                    break;
                case "ask_tool":
                    //System.out.println("ask tool entered");
                    int nrTool = Integer.parseInt(inSocket.readLine());
                    askToolRequest(nrTool);
                    break;
                case "use_tool_type0":
                    //System.out.println("tool 0 entered");
                    String tool0 = inSocket.readLine();
                    useTool(0,tool0);
                    break;
                case "use_tool_type1":
                    //System.out.println("tool 1 entered");
                    String tool1 = inSocket.readLine();
                    useTool(1,tool1);
                    break;
                case "use_tool_type2":
                    //System.out.println("tool 1 entered");
                    String tool2 = inSocket.readLine();
                    useTool(2,tool2);
                    break;
                case "use_tool_type3":
                    //System.out.println("tool 1 entered");
                    String tool3 = inSocket.readLine();
                    useTool(3,tool3);
                    break;
                case "use_tool_type4":
                    //System.out.println("tool 1 entered");
                    String tool4 = inSocket.readLine();
                    useTool(4,tool4);
                    break;
                case "use_tool_type5":
                    //System.out.println("tool 1 entered");
                    String tool5 = inSocket.readLine();
                    useTool(5,tool5);
                    break;

                default:
                    System.out.println("Problem");
            }
        } catch (IOException e) {
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

        assertstuff();
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
        assertstuff();
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

        assertstuff();
        return isAlive;
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
            //boolean reachable = ping();
            //if (reachable)
//            {
                outSocket.write(msg);
                outSocket.flush();
                StringBuilder windows = new StringBuilder(json);
                windows.append("\n");
                outSocket.write(windows.toString());
                outSocket.flush();
                response = inSocket.readLine();
                isAlive = true;
            //} else
              //  isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();
        } catch (Exception e)
        {
            //e.printStackTrace();
            throw new ClientOutOfReachException();
        }

        assertstuff();
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
                isAlive = true;
            } else
                isAlive = false;

            if (!isAlive)
                throw new ClientOutOfReachException();

            startDaemon();

        } catch (Exception e)
        {
            //e.printStackTrace();
            throw new ClientOutOfReachException();
        }
        assertstuff();
        return response;
    }

    /**
     * Recive move from client and process it
     *
     * @param message return message to client
     */
    private void receiveMove(String message)
    {
        try
        {
            ArrayList arr = JSONFacilities.decodeMove(message);
            Pair pair = (Pair)arr.get(0);
            Coordinates coord = (Coordinates)arr.get(1);

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
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Receive from client his intention to pass turn and notify server about it
     */
    private void passTurn()
    {
        try
        {
            System.out.println("pass?");
            outSocket.write("Turn passed\n");
            outSocket.flush();
            notifyServer();
        } catch (Exception e) {
            e.printStackTrace();
            LogFile.addLog("Impossible to notify end turn");
        }
    }

    //</editor-fold>

    //<editor-fold desc="Tool Phase">
    /**
     * Request for using tool from client
     * @param nrTool number of tool required
     */
    private void askToolRequest (int nrTool)
    {
        try
        {
            String s = adapter.toolRequest(nrTool);
            outSocket.write(s + "\n");
            outSocket.flush();
            startDaemon();
        } catch (Exception e) {
            e.printStackTrace();
            LogFile.addLog("Impossible to notify end turn");
        }
    }

    /**
     * use tool from client and return response
     * @param message response to client about using tool
     */
    private void useTool (int type,String message)
    {
        try
        {
            String ret ="Tool non eseguito";
            ArrayList<Wrapper> parameters = JSONFacilities.decodeTool(type,message);

            if (parameters.size() == 0)
                ret = adapter.useTool();
            else if (parameters.size() == 2)
                ret = adapter.useTool(parameters.get(0), parameters.get(1));
            else if (parameters.size() == 3)
                ret = adapter.useTool(parameters.get(0), parameters.get(1),parameters.get(2));
            else if (parameters.size() == 5)
                ret = adapter.useTool(parameters.get(0),parameters.get(1),parameters.get(2),parameters.get(3),parameters.get(4));

            match.updateClient();

            outSocket.write(ret + "\n");
            outSocket.flush();
            startDaemon();
        } catch (JSONException jre){
            jre.printStackTrace();
            LogFile.addLog("Impossible to decrypt JSON");
        }catch (Exception e) {
            e.printStackTrace();
            LogFile.addLog("Impossible to notify use tool");
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

    //<editor-fold desc="Ping test">

    /**
     * ping function to see if the client is reachable
     *
     * @return weather the client is reachable or not
     */
    public boolean ping()
    {
        //ping-pong communication
        boolean reply;
        try
        {
            outSocket.write("ping\n");
            outSocket.flush();
            String r = inSocket.readLine();
            //String r = waitResponse();
            //System.out.println("ping recive:" + r);
            reply = r.equals("pong");
        } catch (IOException|NullPointerException ste)
        {
            //ste.printStackTrace();
            return false;
        }
        assertstuff();
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
        assertstuff();
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

    //<editor-fold desc="Utilities">
    private void startDaemon ()
    {
        try
        {
            deamon = new Thread(this);
            deamon.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void setAdapter(ServerModelAdapter adapter) throws RemoteException
    {
        this.adapter = adapter;
    }

    @Override
    public void setMatchHandler(MatchHandler match) throws RemoteException {
        this.match = match;
    }

    /**
     * Notify server the end of current turn
     */
    private void notifyServer ()
    {
        try{
            adapter.setTurnDone(true);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Reconnection">
    @Override
    public void reconnect() throws RemoteException {
        outSocket.write("reconnect\n");
        outSocket.flush();
    }
    //</editor-fold>

    public void assertstuff() {
        assert !client.isClosed();
    }
}
