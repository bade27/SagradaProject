package it.polimi.ingsw.utilities;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class JSONFacilities {
    //moves
    public static Move decodeMove(String move) throws JSONException {

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
        ColorEnum col  = (ColorEnum) dice.get("col");


        return new Move(new Pair(x, y), new Dice(value, col), player);
    }

    public static JSONObject encodeMove(Move m) throws JSONException {
        JSONObject move = new JSONObject();
        move.put("player", m.getPlayerName());

        JSONObject pair = new JSONObject();
        pair.put("x-coordinate", m.getPair().getX());
        pair.put("y-coordinate", m.getPair().getY());

        JSONObject dice = new JSONObject();
        dice.put("value", 3);
        dice.put("color",  m.getDice().getColor());

        move.put("pair", pair);
        move.put("dice", dice);

        return move;
    }

    //string arrays
    public static JSONArray encodeStringArrays(String[]... s) throws JSONException {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < s.length; i++) {
            JSONArray item = new JSONArray();
            String[] strArr = s[i];
            for (int j = 0; j < strArr.length; j++) {
                item.put(strArr[j]);
            }
            msg.put(item);
        }
        return msg;
    }

    public static ArrayList<String[]> decodeStringArrays(String message) throws JSONException {
        ArrayList<String[]> list = new ArrayList<>();
        JSONArray arrayOfArray = new JSONArray(message);
        for (int i = 0; i < arrayOfArray.length(); i++) {
            JSONArray array = arrayOfArray.getJSONArray(i);
            String[] strArray = new String[array.length()];
            for (int j = 0; j < array.length(); j++) {
                strArray[j] = array.optString(j);
            }
            list.add(strArray);
        }
        return list;
    }
}

class Move {
    private Pair p;
    private Dice d;
    private String playerName;

    public Move(Pair p, Dice d, String player) {
        this.p = p;
        this.d = d;
        this.playerName = player;
    }

    public Pair getPair() {
        return p;
    }

    public Dice getDice() {
        return d;
    }

    public String getPlayerName() {
        return playerName;
    }
}

class Pair {
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}