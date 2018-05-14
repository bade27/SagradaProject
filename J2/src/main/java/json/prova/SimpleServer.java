package json.prova;

import java.awt.Color;

import json.prova.necessity.Cell;
import json.prova.necessity.Dice;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

    private static Cell[][] grid = new Cell[4][5];
    /**
     * fa il parsing della stringa in formato JSON
     * @return la mossa corrispondente
     */
    public static Move parserJSON(String move) {

        //oggetto mossa
        JSONObject obj = new JSONObject(move);

        //nome del giocatore che ha inviato la mossa
        String player = String.valueOf(obj.get("player"));

        //oggetto pair e valori associati
        JSONObject pair = obj.getJSONObject("pair");
        int x = pair.getInt("x-coordinate");
        int y = pair.getInt("y-coordinate");

        //oggetto dado e valori associati
        JSONObject dice = obj.getJSONObject("dice");
        int value = dice.getInt("value");
        //il campo colore è memoriazzato in rgb
        JSONObject rgb = dice.getJSONObject("color");
        int r = rgb.getInt("r");
        int g = rgb.getInt("g");
        int b = rgb.getInt("b");
        Color c = new Color(r, g, b);

        return new Move(new Pair(x, y), new Dice(value, c), player);
    }


    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
            Socket socket = serverSocket.accept();
            System.out.println("Client Connected");
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            while(true) {
                String echoString = input.readLine();
                if(echoString.equals("n")) {
                    break;
                }
                String moveS = input.readLine();
                Move move = parserJSON(moveS);

                int x = move.getPair().getX();
                int y = move.getPair().getY();
                if(grid[y][x] != null)
                    output.println("bad move");
                else {
                    grid[y][x] = new Cell(move.getDice());
                    output.println("ok");
                }
            }


        } catch(IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }
}
