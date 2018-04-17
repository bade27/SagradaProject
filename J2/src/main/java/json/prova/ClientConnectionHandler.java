package json.prova;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientConnectionHandler extends UnicastRemoteObject implements RemoteClient, Runnable {

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private ServerInterface server;


    public ClientConnectionHandler(String type) throws RemoteException {

    }

    public void doStuff() {

    }

    @Override
    public void run() {

    }

    public int[] dealPublicObjective(int[] ids) {
        return new int[0];
    }

    public int[] dealTools(int[] ids) {
        return new int[0];
    }

    public int privateObjective(int id) {
        return 0;
    }

    public int[] dealWindows(int[] ids) {
        return new int[0];
    }

    public boolean login() {
        return true;
    }

    private boolean init() {
        return true;
    }

    private boolean init_rmi() {
        try {
            server = (ServerInterface) Naming.lookup("rmi://" + "/myabc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static JSONObject toJSON(Move m) {
        JSONObject move = new JSONObject();
        move.put("player", m.getPlayerName());

        JSONObject pair = new JSONObject();
        pair.put("x-coordinate", m.getPair().getX());
        pair.put("y-coordinate", m.getPair().getY());

        JSONObject dice = new JSONObject();
        dice.put("value", 3);
        JSONObject rgb = new JSONObject();
        rgb.put("r", m.getDice().getColor().getRed());
        rgb.put("g", m.getDice().getColor().getGreen());
        rgb.put("b", m.getDice().getColor().getBlue());
        dice.put("color", rgb);

        move.put("pair", pair);
        move.put("dice", dice);

        return move;
    }
}
