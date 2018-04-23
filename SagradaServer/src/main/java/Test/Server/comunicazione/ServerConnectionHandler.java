import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler {

    private Socket client;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    public static int PORT=3000;
    private ServerSocket serverSocket;

    public ServerConnectionHandler() {
        try {
            init_connection();
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
        } catch (IOException e) {
            try {
                client.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private void init_connection() {
        serverSocket=null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("\nServer waiting for client on port " +  serverSocket.getLocalPort());

            // server infinite loop
            for(int i = 0; i < 1000; i++) {
                client = serverSocket.accept();
            }
        }
        catch(Exception e) {
            System.out.println(e);
            try {
                serverSocket.close();
            } catch(Exception ex) {

            }
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String chooseWindow(String[] s1, String[] s2) {

        String response = "";

        try {
            JSONArray jsonArray = JSONFacilities.encodeStringArrays(s1, s2); //creo il json da inviare
            outSocket.println("Scegli la vetrata");
            outSocket.println(jsonArray.toString());    //invio le due coppie di vetrate
            response = inSocket.readLine();  //mi aspetto il nome della vetrata scelta
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return response;

    }

    public void sendPrivateObjective() {
        //mi aspetto un ok come risposta
    }

    public void sendCards(String[]... s) {
        //mi aspetto un ok come risposta
    }

    public void moves() {
        //dico al client che è il suo turno, poi aspetto che invii la mossa fatta
    }

    public void close() {
        try {
            client.close();
        } catch(Exception e) {
            System.out.println("Exception: e="+e);
            e.printStackTrace();

            try {
                client.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
