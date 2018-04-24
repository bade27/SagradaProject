package Test.Client;

import Test.Server.JSONFacilities;
import Test.Client.Client;
import org.json.JSONException;

import java.io.*;
import java.net.Socket;

public class ClientConnectionHandler implements Runnable {
    private final String address = "localhost";
    private final int PORT = 3000;
    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private Client c;
    private Thread deamon;

    public ClientConnectionHandler(Client c) {
        this.c = c;
        System.out.println("connecting...");
        try{
            socket = new Socket(address, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connected");
        deamon = new Thread(this);
        deamon.start();
    }

    @Override
    public void run() {

        try {
            String action = "";
            while( (action = inSocket.readLine()) != null )  {
                switch (action) {
                    case "windowinit":
                        chooseWindow();
                        continue;
                    case "privobj":
                        myPrivateObj();
                        continue;
                    default:
                        return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void chooseWindow() {
        try {
            System.out.println(inSocket.readLine());
            String json = inSocket.readLine();
            String choice = c.chooseWindow(JSONFacilities.decodeStringArrays(json));
            outSocket.println(choice);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public synchronized void myPrivateObj() {
        try {
            String response = c.myPrivateObj(inSocket.readLine()).toLowerCase();
            outSocket.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
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
        }
    }

}
