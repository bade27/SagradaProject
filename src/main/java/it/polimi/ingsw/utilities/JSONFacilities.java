package it.polimi.ingsw.utilities;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONFacilities
{

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

    //<editor-fold desc="JSON for Dadiera">
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

    //<editor-fold desc="JSON for Window and RoundTrace">

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

    //<editor-fold desc="JSON for Tokens">
    public static JSONObject encodeInteger (Integer num) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("number",num);
        return json;
    }

    public static int decodeInteger (String message) throws JSONException
    {
        JSONObject json = new JSONObject(message);
        return (Integer)json.get("number");
    }
    //</editor-fold>

    //<editor-fold desc="JSON for end game results">
    public static JSONArray encodeStringInteger (String[] user, int[] point) throws JSONException
    {
        JSONArray msg = new JSONArray();
        for (int i = 0; i < user.length; i++)
        {
            JSONObject item = new JSONObject();
            item.put("user",user[i]);
            item.put("points",point[i]);
            msg.put(item);
        }
        return msg;
    }

    public static String[][] decodeStringInteger (String message) throws JSONException
    {
        JSONArray msg = new JSONArray(message);
        String [][] ret = new String[msg.length()][];
        for (int i = 0; i < msg.length() ; i++)
        {
            JSONObject obj = (JSONObject) msg.get(i);
            ret[i] = new String [2];
            ret[i][0]=(String)obj.get("user");
            ret[i][1]=((Integer)obj.get("points")).toString();
        }
        return ret;
    }
    //</editor-fold>

    //<editor-fold desc="JSON for move">
    public static JSONArray encodeMove (Coordinates coord, Pair pair) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodePair(pair));
        jsonArray.put(encodeCoordinates(coord));
        return jsonArray;
    }

    public static ArrayList decodeMove (String message) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(message);
        ArrayList ret = new ArrayList();
        ret.add(decodePair((JSONObject)jsonArray.get(0)));
        ret.add(decodeCoordinates((JSONObject)jsonArray.get(1)));
        return ret;
    }
    //</editor-fold>


    //<editor-fold desc="JSON for tools">

    /**
     * Encode tool overload for tool type 0
     * @param p pair to encode
     * @param s string to encode
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool (Pair p, String s) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodePair(p));
        jsonArray.put(s);
        return jsonArray;
    }

    /**
     * Encode tool overload for tool type 1
     * @param c1 coordinate 1 to encode
     * @param c2 coordinate 2 to encode
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool (Coordinates c1,Coordinates c2) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodeCoordinates(c1));
        jsonArray.put(encodeCoordinates(c2));
        return jsonArray;
    }

    /**
     * Encode tool overload for tool type 2
     * @param traceDie Pair from round trace to encode
     * @param c1S coordinate 1 start to encode
     * @param c1E coordinate 1 end to encode
     * @param c2S coordinate 2 start to encode
     * @param c2E coordinate 2 end to encode
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool (Pair traceDie ,Coordinates c1S,Coordinates c1E,Coordinates c2S,Coordinates c2E) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodePair(traceDie));
        jsonArray.put(encodeCoordinates(c1S));
        jsonArray.put(encodeCoordinates(c1E));
        jsonArray.put(encodeCoordinates(c2S));
        jsonArray.put(encodeCoordinates(c2E));
        return jsonArray;
    }

    /**
     * Encode tool overload for tool type 3
     * @param dadieraDie Pair from dadiera to encode
     * @param traceDie  Pair from round trace to encode
     * @param tracePosition position of round trace
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool (Pair dadieraDie, Pair traceDie, int tracePosition) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodePair(dadieraDie));
        jsonArray.put(encodePair(traceDie));
        jsonArray.put(tracePosition);
        return jsonArray;
    }

    /**
     * Encode tool overload for tool type 3 (no parameters needed)
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool () throws JSONException
    {
        return new JSONArray();
    }

    /**
     * Encode tool overload for tool type 3 (no parameters needed)
     * @return JSON with encoded parameters
     */
    public static JSONArray encodeTool(Pair dadieraDie,Coordinates fstDieStartPosition)throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(encodePair(dadieraDie));
        jsonArray.put(encodeCoordinates(fstDieStartPosition));
        return jsonArray;
    }

    /**
     * Decode JSON of tool
     * @param type type of tool to decode
     * @param message JSON to decode
     * @return ArrayList of Wrapper of Tools' parameters
     */
    public static ArrayList decodeTool (int type,String message) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(message);
        ArrayList<Wrapper> ret = new ArrayList<Wrapper>();
        switch (type)
        {
            case 0:
                ret.add(new Wrapper(decodePair((JSONObject)jsonArray.get(0))));
                ret.add(new Wrapper(jsonArray.get(1)));
                break;
            case 1:
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(0))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(1))));
                break;
            case 2:
                ret.add(new Wrapper(decodePair((JSONObject)jsonArray.get(0))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(1))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(2))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(3))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(4))));
                break;
            case 3:
                ret.add(new Wrapper(decodePair((JSONObject)jsonArray.get(0))));
                ret.add(new Wrapper(decodePair((JSONObject)jsonArray.get(1))));
                ret.add(new Wrapper(jsonArray.get(2)));
                break;
            case 4:
                break;
            case 5:
                ret.add(new Wrapper(decodePair((JSONObject)jsonArray.get(0))));
                ret.add(new Wrapper(decodeCoordinates((JSONObject)jsonArray.get(1))));
                break;
        }

        return ret;
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    private static JSONObject encodePair (Pair p) throws JSONException
    {
        JSONObject obj = new JSONObject();
        if (p == null)
            obj.put("enable",false);
        else
        {
            obj.put("enable",true);
            if (p.getColor() != null)
                obj.put("color",p.getColor().toString());
            else
                obj.put("color","n/d");
            obj.put("value",p.getValue());
        }
        return obj;
    }

    private static Pair decodePair (JSONObject obj) throws JSONException
    {
        if (!(Boolean)obj.get("enable"))
            return null;
        return new Pair((int)obj.get("value"),ColorEnum.getColor((String)obj.get("color")));
    }

    private static JSONObject encodeCoordinates (Coordinates c) throws JSONException
    {
        JSONObject obj = new JSONObject();
        if (c == null)
            obj.put("enable",false);
        else
        {
            obj.put("enable",true);
            obj.put("coord_i",c.getI());
            obj.put("coord_j",c.getJ());
        }
        return obj;
    }

    private static Coordinates decodeCoordinates (JSONObject obj) throws JSONException
    {
        if (!(Boolean)obj.get("enable"))
            return null;
        return new Coordinates((Integer)obj.get("coord_i"),(Integer)obj.get("coord_j"));
    }
    //</editor-fold>

}