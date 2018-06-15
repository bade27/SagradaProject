package it.polimi.ingsw.utilities;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONFacilities {
    //moves
    /*public static Move decodeMove(String move) throws JSONException {

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
    }*/

    /*public static JSONObject encodeMove(Move m) throws JSONException {
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
    }*/


    //<editor-fold desc="JSON for cards">
    /**
     * Encode strings' array into JSON
     * @param s String's array
     * @return JSON
     */
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

    /**
     * Decode JSON to ArrayList of strings's array
     * @param message JSON
     * @return ArrayList of strings's array
     */
    public static ArrayList<String[]> decodeStringArrays(String message) throws JSONException
    {
        ArrayList<String[]> list = new ArrayList<>();
        JSONArray arrayOfArray = new JSONArray(message);
        for (int i = 0; i < arrayOfArray.length(); i++)
        {
            JSONArray array = arrayOfArray.getJSONArray(i);
            String[] strArray = new String[array.length()];
            for (int j = 0; j < array.length(); j++)
            {
                strArray[j] = array.optString(j);
            }
            list.add(strArray);
        }
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="JSON for Array of Pair">
    /**
     * Encode an array of Pair into JSON
     * @param toEncode array of Pair
     * @return JSON
     */
    public static JSONArray encodeArrayPair (Pair[] toEncode) throws JSONException {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < toEncode.length; i++)
        {
            JSONObject jsonObj = new JSONObject();
            if (toEncode[i].getColor() != null)
                jsonObj.put("color",toEncode[i].getColor().toString());
            else
                jsonObj.put("color","n/d");
            jsonObj.put("value",toEncode[i].getValue());
            msg.put(jsonObj);
        }
        return msg;
    }

    /**
     * Encode an arrayList of Pair into JSON
     * @param toEncode arrayList of Pair
     * @return JSON
     */
    private static JSONArray encodeArrayPair (ArrayList<Pair> toEncode) throws JSONException {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < toEncode.size(); i++)
        {
            JSONObject jsonObj = new JSONObject();
            if (toEncode.get(i).getColor() != null)
                jsonObj.put("color",toEncode.get(i).getColor().toString());
            else
                jsonObj.put("color","n/d");
            jsonObj.put("value",toEncode.get(i).getValue());
            msg.put(jsonObj);
        }
        return msg;
    }

    /**
     * Decode a JSON into an ArrayList of Pair
     * @param message JSON
     * @return ArrayList of Pair
     */
    public static ArrayList<Pair> decodeArrayPair (String message) throws JSONException
    {
        JSONArray json = new JSONArray(message);
        return decodeArrayPair(json);
    }

    //Used in upper function
    private static ArrayList<Pair> decodeArrayPair (JSONArray arr) throws JSONException
    {
        ArrayList<Pair> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++)
        {
            JSONObject obj = (JSONObject)arr.get(i);
            list.add(new Pair((int)obj.get("value"),ColorEnum.getColor((String)obj.get("color"))));
        }
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="JSON for matrix of pair">

    /**
     * Encode into Json a matrix of Pair and a string
     * @param toEncode matrix of Pair
     * @return json
     */
    public static JSONArray encodeMatrixPair (String str ,Pair[][] toEncode) throws JSONException {
        JSONArray msg = new JSONArray();
        msg.put(str);
        for (int i = 0; i < toEncode.length; i++)
        {
            JSONArray arr = encodeArrayPair(toEncode[i]);
            msg.put(arr);
        }
        return msg;
    }

    /**
     * Encode into Json a matrix of Pair
     * @param toEncode matrix of Pair
     * @return json
     */
    public static JSONArray encodeMatrixPair (Pair[][] toEncode) throws JSONException {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < toEncode.length; i++)
        {
            JSONArray arr = encodeArrayPair(toEncode[i]);
            msg.put(arr);
        }
        return msg;
    }

    /**
     * Encode into Json a matrix of Pair
     * @param toEncode matrix of Pair
     * @return json
     */
    public static JSONArray encodeMatrixPair (ArrayList<Pair>[] toEncode) throws JSONException {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < toEncode.length; i++)
        {
            JSONArray arr = encodeArrayPair(toEncode[i]);
            msg.put(arr);
        }
        return msg;
    }

    /**
     * Decode from json a matrix of Pair
     * @param message json
     * @return matrix of pair
     */
    public static ArrayList<ArrayList<Pair>> decodeMatrixPair (String message) throws JSONException
    {
        ArrayList<ArrayList<Pair>> list = new ArrayList<>();
        JSONArray matrix = new JSONArray(message);
        for (int i = 0; i < matrix.length(); i++)
        {
            JSONArray row = (JSONArray)matrix.get(i);
            list.add(decodeArrayPair(row));
        }
        return list;
    }

    /**
     * Decode from json a matrix of Pair with a string in first position
     * @param message json
     * @return matrix of pair
     */
    public static ArrayList<ArrayList<Pair>> decodeMatrixPairWithString (String message) throws JSONException
    {
        ArrayList<ArrayList<Pair>> list = new ArrayList<>();
        JSONArray matrix = new JSONArray(message);
        for (int i = 1; i < matrix.length(); i++)
        {
            JSONArray row = (JSONArray)matrix.get(i);
            list.add(decodeArrayPair(row));
        }
        return list;
    }

    public static String decodeStringInMatrixPair (String message) throws JSONException
    {
        JSONArray matrix = new JSONArray(message);
        return matrix.get(0).toString();
    }

    //</editor-fold>

    //<editor-fold desc="JSON for Integer">
    public static JSONObject encodeInteger (Integer num)
    {
        JSONObject json = new JSONObject();
        json.put("number",num);
        return json;
    }

    public static int decodeInteger (String message)
    {
        JSONObject json = new JSONObject(message);
        return (Integer)json.get("number");
    }
    //</editor-fold>


}