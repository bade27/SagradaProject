package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.utilities.JSONFacilities;
import org.json.JSONException;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientSocketHandler implements Runnable,ServerRemoteInterface {

    private String HOSTNAME;
    private int PORT;
    private int INIT_EXECUTE_TIME;
    private int MOVE_EXECUTE_TIME;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    private Thread deamon;

    private ClientPlayer player;

    public ClientSocketHandler(ClientPlayer cli, String host, int port, int init_time, int mov_time) throws ClientOutOfReachException {
        player = cli;
        HOSTNAME = host;
        PORT = port;
        INIT_EXECUTE_TIME = init_time;
        MOVE_EXECUTE_TIME = mov_time;
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

        try {
            String action = "";
            int executionTime = 0;
            ExecutorService executor = Executors.newFixedThreadPool(1);
            while( (action = inSocket.readLine()) != "close" )  {
                switch (action) {
                    case "windowinit":
                        chooseWindow();
                        continue;
                    case "login":
                        login();
                        continue;
                    case "pub_objs":
                        receivePublicObjectives();
                        continue;
                    case "ping":
                        player.ping();
                        outSocket.write("pong\n");
                        outSocket.flush();
                        continue;
                    default:
                        System.out.println(action);
                        continue;
                }
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Boolean chooseWindow()
    {
        try {
            System.out.println(inSocket.readLine());
            String json = inSocket.readLine();
            StringBuilder choice = new StringBuilder(player.chooseWindow(JSONFacilities.decodeStringArrays(json)));
            choice.append("\n");
            outSocket.write(choice.toString());
            return outSocket.checkError();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean receivePublicObjectives()
    {
        try {
            String objs = inSocket.readLine();
            System.out.println(objs);
            outSocket.write("ok\n");
            return outSocket.checkError();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /*public void myPrivateObj() {
        try {
            String response = graph.myPrivateObj(inSocket.readLine()).toLowerCase();
            outSocket.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private Boolean login() {
        try {
            //Da modificare con finestra a popup con username
            inSocket.readLine();
            StringBuilder username = new StringBuilder(player.login());
            username.append("\n");
            outSocket.write(username.toString());
            return outSocket.checkError();
        } catch (IOException | ClientOutOfReachException e) {
            e.printStackTrace();
        }
        return false;
    }

    private <T> void stopTask(Callable<T> task, int executionTime, ExecutorService executor) {
        Future future = executor.submit(task);
        try {
            future.get(executionTime, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            //System.out.println(te.getMessage());
            System.out.println("too late to reply");
        } catch (InterruptedException ie) {
            //System.out.println(ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException ee) {
            //System.out.println(ee.getMessage());
        } finally {
            future.cancel(true);
        }
    }

    private void close() {
        String msg = "";
        try {
            msg = inSocket.readLine();
            socket.close();
        } catch(Exception e) {
            System.out.println("Exception: "+e);
            e.printStackTrace();
        } finally {
            // Always close it:
            try {
                socket.close();
            } catch(IOException ex) {
                System.err.println("Socket not closed");
            }
            player.closeComunication(msg);
        }
    }

    //Sono obbligato ad implementarlo, per ora non ha uno scopo preciso
    public void setClient (ClientRemoteInterface client)
    {
    }
}
