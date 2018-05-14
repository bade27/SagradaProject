package json.prova;

import org.json.JSONObject;
import java.awt.Color;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.net.Socket;
import json.prova.necessity.*;

public class SimpleClient {

    private static String name = "bob";

    public static Move randomMove() {

        //elementi che compongono la mossa
        int x = new Random().nextInt(5);
        int y = new Random().nextInt(4);
        int value = new Random().nextInt(6) + 1;
        int r = new Random().nextInt(256);
        int g = new Random().nextInt(256);
        int b = new Random().nextInt(256);
        Color c = new Color(r, g, b);;

        return new Move(new Pair(x, y), new Dice(value, c), name);
    }

    /**
     *
     * @param m *mossa del giocatore*
     * @return *la mossa del giocatore in formato JSON,
     * pronta per essere inviata al server*
     */
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

    //main
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            BufferedReader echoes = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            String action;
            String response;

            do {
                System.out.println("wanna play?[y/n]");
                action = scanner.nextLine();

                output.println(action);
                if(!action.equals("n")) {
                    Move my_move = randomMove();
                    JSONObject my_moveJ = toJSON(my_move);
                    output.println(my_moveJ.toString());
                    response = echoes.readLine();
                    System.out.println(response);
                }
            } while(!action.equals("n"));

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());

        }
    }
}