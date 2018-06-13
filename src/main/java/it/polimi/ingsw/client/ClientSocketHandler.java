package it.polimi.ingsw.client;

import com.sun.org.apache.xpath.internal.operations.Bool;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.*;
import it.polimi.ingsw.utilities.JSONFacilities;
import org.json.JSONException;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientSocketHandler implements Runnable,ServerRemoteInterface {

    private String HOSTNAME;
    private int PORT;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    private Thread deamon;

    private ClientPlayer player;

    public ClientSocketHandler(ClientPlayer cli, String host, int port) throws ClientOutOfReachException {
        player = cli;
        HOSTNAME = host;
        PORT = port;
        socket = null;
        try {
            System.out.println("Socket connection to host " + HOSTNAME + " port " + PORT + "...");
            socket = new Socket(HOSTNAME, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClientOutOfReachException();
        }
        deamon = new Thread(this);
        deamon.start();
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<?> task = null;
        String action = "";
        boolean stop = false;
        try {
            while( (action = inSocket.readLine()) != "close" && action != null)  {
                switch (action) {
                    case "ping":
                        outSocket.write("pong\n");
                        outSocket.flush();
                        continue;
                    case "login":
                        task = executor.submit(() -> {login();});
                        continue;
                    case "cards":
                        String objs = inSocket.readLine();
                        task = executor.submit(() -> {receiveCards(objs);});
                        continue;
                    case "windowinit":
                        String json = inSocket.readLine();
                        task = executor.submit(() -> {chooseWindow(json);});
                        continue;
                    case "up_dadiera":
                        String dad = inSocket.readLine();
                        task = executor.submit(() -> {updateDadiera(dad);});
                        continue;
                    case "up_window":
                        String win = inSocket.readLine();
                        task = executor.submit(() -> {updateWindow(win);});
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
                        //System.out.println(action);
                        continue;
                }
                if(stop)
                    break;
            }
            close(task);
        } catch (IOException e) {
            //e.printStackTrace();
            close(task);
        }

    }

    private Boolean chooseWindow(String json)
    {
        try {
            StringBuilder choice = new StringBuilder(player.chooseWindow(JSONFacilities.decodeStringArrays(json)));
            choice.append("\n");
            outSocket.write(choice.toString());
            return outSocket.checkError();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean receiveCards(String json)
    {
        try {
            ArrayList<String[]> arr = JSONFacilities.decodeStringArrays(json);
            player.sendCards(arr.get(0),arr.get(1),arr.get(2));
            outSocket.write("ok\n");
            return outSocket.checkError();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateDadiera (String json)
    {
        try {
            ArrayList<Pair> arr = JSONFacilities.decodeArrayPair(json);
            Pair[] dices = new Pair[arr.size()];
            for (int i = 0 ; i < arr.size() ; i++)
                dices[i] = arr.get(i);
            player.updateGraphic(dices);
            outSocket.write("ok\n");
            return outSocket.checkError();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean updateWindow (String json)
    {
        try {
            ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(json);
            Pair[][] board = new Pair[list.size()][];
            for (int i = 0 ; i < list.size() ; i++)
            {
                board[i] = new Pair[list.get(i).size()];
                for (int j = 0 ; j < list.get(i).size() ; j++)
                    board[i][j] = list.get(i).get(j);
            }
            player.updateGraphic(board);
            outSocket.write("ok\n");
            return outSocket.checkError();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean login() {
        try {
            //Da modificare con finestra a popup con username
            StringBuilder username = new StringBuilder(player.login());
            username.append("\n");
            outSocket.write(username.toString());
            return outSocket.checkError();
        } catch (ClientOutOfReachException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void close(Future<?> task) {
        String msg = "";
        try {
            msg = inSocket.readLine();
            if(msg == null)
                throw new NullPointerException();
            task.cancel(true);
            socket.close();
            System.out.println("socket closed");
        } catch(Exception e) {
            //System.out.println("Exception: "+e);
            //e.printStackTrace();
            msg = "server ended communication";
        } finally {
            try {
                socket.close();
            } catch(IOException ex) {
                System.err.println("Socket not closed");
            }
            player.closeCommunication(msg);
        }
    }

    //Sono obbligato ad implementarlo, per ora non ha uno scopo preciso
    public void setClient (ClientRemoteInterface client)
    {
    }

    @Override
    public String makeMove(Coordinates coord, Pair pair) throws RemoteException {
        return null;
    }


    @Override
    public String passTurn() throws RemoteException {
        return null;
    }

    @Override
    public String askToolPermission(int nrTool) throws RemoteException {
        return null;
    }

    @Override
    public String useTool(Pair p, String ins) throws RemoteException {
        return null;
    }

    @Override
    public String useTool( Coordinates sartCoord, Coordinates endCoord) throws RemoteException {
        return null;
    }

    @Override
    public String useTool(Pair p, Coordinates sartCoord1, Coordinates endCoord1, Coordinates sartCoord2, Coordinates endCoord2) throws RemoteException {
        return null;
    }

    @Override
    public String useTool(Pair dadiera, Pair trace, int nrRound) throws RemoteException {
        return null;
    }

    @Override
    public String useTool() throws RemoteException {
        return null;
    }

    @Override
    public String useTool(Pair p, Coordinates endCoord) throws RemoteException {
        return null;
    }


}
