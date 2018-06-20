package it.polimi.ingsw.client;


import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.*;
import it.polimi.ingsw.utilities.JSONFacilities;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientSocketHandler implements Runnable, ServerRemoteInterface
{

    private String HOSTNAME;
    private int PORT;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    private Thread deamon;

    private ClientPlayer player;
    private final Integer syncronator = 5;
    private String action;

    public ClientSocketHandler(ClientPlayer cli, String host, int port) throws ClientOutOfReachException
    {
        player = cli;
        HOSTNAME = host;
        PORT = port;
        socket = null;
        try
        {
            System.out.println("Socket connection to host " + HOSTNAME + " port " + PORT + "...");
            socket = new Socket(HOSTNAME, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e)
        {
            //e.printStackTrace();
            throw new ClientOutOfReachException();
        }
        deamon = new Thread(this);
        deamon.start();
    }

    @Override
    public void run()
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<?> task = null;
        action = null;
        boolean stop = false;
        try
        {
            while ((action = inSocket.readLine()) != "close" && action != null)
            {
                switch (action)
                {
                    case "ping":
                        outSocket.write("pong\n");
                        outSocket.flush();
                        action = null;
                        continue;
                    case "login":
                        task = executor.submit(() -> {login();});
                        action = null;
                        continue;
                    case "cards":
                        String objs = inSocket.readLine();
                        task = executor.submit(() -> {receiveCards(objs);});
                        action = null;
                        continue;
                    case "windowinit":
                        String json = inSocket.readLine();
                        task = executor.submit(() -> {chooseWindow(json);});
                        action = null;
                        continue;
                    case "up_dadiera":
                        String dad = inSocket.readLine();
                        task = executor.submit(() -> {updateDadiera(dad);});
                        action = null;
                        continue;
                    case "up_window":
                        String win = inSocket.readLine();
                        task = executor.submit(() -> {updateWindow(win);});
                        action = null;
                        continue;
                    case "up_trace":
                        String tra = inSocket.readLine();
                        task = executor.submit(() -> {updateRoundTrace(tra);});
                        action = null;
                        continue;
                    case "up_tokens":
                        String tok = inSocket.readLine();
                        task = executor.submit(() -> {updateTokens(tok);});
                        action = null;
                        continue;
                    case "up_opponents":
                        String opp = inSocket.readLine();
                        task = executor.submit(() -> {updateOpponents(opp);});
                        action = null;
                        continue;
                    case "up_results":
                        String res = inSocket.readLine();
                        task = executor.submit(() -> {getResults(res);});
                        action = null;
                        continue;
                    case "doTurn":
                        task = executor.submit(() -> {doTurn();});
                        action = null;
                        continue;
                    case "close":
                        stop = true;
                        break;
                    case "msg":
                        String msg = inSocket.readLine();
                        player.sendMessage(msg);
                        continue;
                    case "content":
                        continue;
                    default:
                        synchronized (syncronator) {
                            syncronator.notifyAll();
                        }
                        continue;
                }
                if (stop)
                    break;
            }
            close(task);
        } catch (IOException e)
        {
            //e.printStackTrace();
            close(task);
        }

    }

    //<editor-fold desc="Setup Phase">
    private Boolean login()
    {
        try
        {
            //Da modificare con finestra a popup con username
            StringBuilder username = new StringBuilder(player.login());
            username.append("\n");
            outSocket.write(username.toString());
            return outSocket.checkError();
        } catch (ClientOutOfReachException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean chooseWindow(String json)
    {
        try
        {
            StringBuilder choice = new StringBuilder(player.chooseWindow(JSONFacilities.decodeStringArrays(json)));
            choice.append("\n");
            outSocket.write(choice.toString());
            return outSocket.checkError();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean receiveCards(String json)
    {
        try
        {
            ArrayList<String[]> arr = JSONFacilities.decodeStringArrays(json);
            player.sendCards(arr.get(0), arr.get(1), arr.get(2));
            outSocket.write("ok\n");
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Update Client">
    private Boolean updateDadiera(String json)
    {
        try
        {
            ArrayList<Pair> arr = JSONFacilities.decodeArrayPair(json);
            Pair[] dices = new Pair[arr.size()];
            for (int i = 0; i < arr.size(); i++)
                dices[i] = arr.get(i);
            player.updateGraphic(dices);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateWindow(String json)
    {
        try
        {
            ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(json);
            Pair[][] board = new Pair[list.size()][];
            for (int i = 0; i < list.size(); i++)
            {
                board[i] = new Pair[list.get(i).size()];
                for (int j = 0; j < list.get(i).size(); j++)
                    board[i][j] = list.get(i).get(j);
            }
            player.updateGraphic(board);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateRoundTrace(String json)
    {
        try
        {
            ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(json);
            ArrayList<Pair>[] round = new ArrayList[list.size()];
            for (int i = 0; i < list.size(); i++)
                round[i] = list.get(i);

            player.updateRoundTrace(round);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateTokens(String json)
    {
        try
        {
            int toks = JSONFacilities.decodeInteger(json);
            player.updateTokens(toks);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateOpponents(String json)
    {
        try
        {
            String user = JSONFacilities.decodeStringInMatrixPair(json);
            ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPairWithString(json);
            Pair[][] board = new Pair[list.size()][];
            for (int i = 0; i < list.size(); i++)
            {
                board[i] = new Pair[list.get(i).size()];
                for (int j = 0; j < list.get(i).size(); j++)
                    board[i][j] = list.get(i).get(j);
            }
            player.updateOpponents(user, board);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Turn Phase">
    private Boolean doTurn()
    {
        player.doTurn();
        outSocket.write("ok\n");
        outSocket.flush();
        return outSocket.checkError();
    }

    @Override
    public String makeMove(Coordinates coord, Pair pair) throws RemoteException
    {
        String response = "";

        try
        {
            JSONArray json = JSONFacilities.encodeMove(coord,pair);
            outSocket.write("move\n");
            outSocket.flush();
            StringBuilder move = new StringBuilder(json.toString());
            move.append("\n");
            outSocket.write(move.toString());
            outSocket.flush();
            response = waitResponse();

        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String passTurn() throws RemoteException
    {
        String response = "";
        try
        {
            outSocket.write("pass_turn\n");
            outSocket.flush();
            response = waitResponse();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold desc="Tool Phase">
    @Override
    public String askToolPermission(int nrTool) throws RemoteException
    {
        String response = "";
        try
        {
            outSocket.write("ask_tool\n");
            outSocket.flush();
            outSocket.write(nrTool + "\n");
            outSocket.flush();
            response = waitResponse();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String sendTool (String msg,String json)
    {
        String response = "";
        try
        {
            outSocket.write(msg + "\n");
            outSocket.flush();
            StringBuilder move = new StringBuilder(json);
            move.append("\n");
            outSocket.write(move.toString());
            outSocket.flush();
            response = waitResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool(Pair p, String ins) throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool(p,ins);
            response = sendTool("use_tool_type0",json.toString());
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool(Coordinates sartCoord, Coordinates endCoord) throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool(sartCoord,endCoord);
            response = sendTool("use_tool_type1",json.toString());
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool(Pair p, Coordinates sartCoord1, Coordinates endCoord1, Coordinates sartCoord2, Coordinates endCoord2) throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool(p,sartCoord1,endCoord1,sartCoord2,endCoord2);
            response = sendTool("use_tool_type2",json.toString());
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool(Pair dadiera, Pair trace, int nrRound) throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool(dadiera,trace,nrRound);
            response = sendTool("use_tool_type3",json.toString());
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool() throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool();
            response = sendTool("use_tool_type4",json.toString());
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return response;
    }

    @Override
    public String useTool(Pair p, Coordinates endCoord) throws RemoteException
    {
        String response = "";
        try
        {
            JSONArray json = JSONFacilities.encodeTool(p,endCoord);
            response = sendTool("use_tool_type5",json.toString());
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return response;
    }
    //</editor-fold>

    //<editor-fold desc="End Game Phase">
    private Boolean getResults(String json)
    {
        try
        {
            String[][] recived = JSONFacilities.decodeStringInteger(json);
            String[] user = new String[recived.length];
            int[] point = new int[recived.length];
            for (int i = 0; i < recived.length; i++)
            {
                user[i] = recived[i][0];
                point[i] = Integer.parseInt(recived[i][1]);
            }
            player.sendResults(user, point);
            outSocket.write("ok\n");
            outSocket.flush();
            return outSocket.checkError();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    private void close(Future<?> task)
    {
        String msg = "";
        try
        {
            msg = inSocket.readLine();
            if (msg == null)
                throw new NullPointerException();
            task.cancel(true);
            socket.close();
            System.out.println("socket closed");
        } catch (Exception e)
        {
            //System.out.println("Exception: "+e);
            //e.printStackTrace();
            msg = "server ended communication";
        } finally
        {
            try
            {
                socket.close();
            } catch (IOException ex)
            {
                System.err.println("Socket not closed");
            }
            player.closeCommunication(msg);
        }
    }

    //Sono obbligato ad implementarlo, per ora non ha uno scopo preciso
    public void setClient(ClientRemoteInterface client)
    {
    }
    //</editor-fold>

    //<editor-fold desc="Wait Response">
    private String waitResponse () throws RemoteException
    {
        if(!isReachable())
            throw new RemoteException();
        try
        {
            synchronized (syncronator) {
                while (action == null)
                    syncronator.wait();
            }
            String r = action;
            action = null;
            return r;
        }catch (Exception e){
            e.printStackTrace();
            throw new RemoteException();
        }

    }
    //</editor-fold>


    //it is only called with RMI, so it has no function here with sockets
    @Override
    public String serverStatus() {
        return null;
    }

    private boolean isReachable() {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(HOSTNAME, PORT), 5000);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
