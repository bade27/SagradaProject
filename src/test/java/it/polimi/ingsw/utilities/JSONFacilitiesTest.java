package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JSONFacilitiesTest
{
    Window w;
    RoundTrace t;

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws ParserXMLException, IllegalDiceException
    {
        w = new Window("resources/vetrate/xml/kaleidoscopic_dream.xml");
        w.addDice(0, 0, new Dice(1, ColorEnum.YELLOW), 0);
        w.addDice(0, 1, new Dice(5, ColorEnum.BLUE), 0);

        t = new RoundTrace();
        t.addDice(1, new Dice(1, ColorEnum.YELLOW));
        t.addDice(1, new Dice(2, ColorEnum.RED));
        t.addDice(2, new Dice(1, ColorEnum.BLUE));
    }

    @Test
    void encodeAndDecodeMatrixPair() throws ParserXMLException, IllegalDiceException, JSONException
    {
        Pair[][] original = w.getPairMatrix();
        JSONArray arr = JSONFacilities.encodeMatrixPair(original);
        StringBuilder windows = new StringBuilder(arr.toString());
        windows.append("\n");

        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(windows.toString());

        Pair[][] board = new Pair[list.size()][];
        for (int i = 0; i < list.size(); i++)
        {
            board[i] = new Pair[list.get(i).size()];
            for (int j = 0; j < list.get(i).size(); j++)
                board[i][j] = list.get(i).get(j);
        }

        for (int i = 0; i < list.size(); i++)
        {
            for (int j = 0; j < list.get(i).size(); j++)
            {
                assertEquals(board[i][j].getColor(), original[i][j].getColor());
                assertEquals(board[i][j].getValue(), original[i][j].getValue());
            }
        }
    }


    @Test
    void encodeAndDecodeListArrayPair() throws ParserXMLException, IllegalDiceException, JSONException
    {
        ArrayList<Pair>[] original = t.getPair();
        JSONArray arr = JSONFacilities.encodeMatrixPair(original);
        StringBuilder trace = new StringBuilder(arr.toString());
        trace.append("\n");

        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(trace.toString());

        ArrayList<Pair>[] round = new ArrayList[list.size()];
        for (int i = 0; i < list.size(); i++)
            round[i] = list.get(i);

        for (int i = 0; i < round.length; i++)
        {
            for (int j = 0; j < round[i].size(); j++)
            {
                assertEquals(round[i].get(j).getColor(), original[i].get(j).getColor());
                assertEquals(round[i].get(j).getValue(), original[i].get(j).getValue());
            }
        }
    }

    @Test
    void encodeAndDecodeNumber() throws JSONException
    {
        int original = 5;
        JSONObject obj = JSONFacilities.encodeInteger(original);
        StringBuilder trace = new StringBuilder(obj.toString());
        trace.append("\n");

        int decoded = JSONFacilities.decodeInteger(trace.toString());
        assertEquals(original, decoded);
    }

    @Test
    void encodeANdDecodeMatrixPairWithString() throws JSONException
    {
        String user = "KIMOSABE";
        Pair[][] original = w.getPairMatrix();
        JSONArray arr = JSONFacilities.encodeMatrixPair(user, original);
        StringBuilder windows = new StringBuilder(arr.toString());
        windows.append("\n");

        String recived = JSONFacilities.decodeStringInMatrixPair(windows.toString());
        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPairWithString(windows.toString());

        Pair[][] board = new Pair[list.size()][];
        for (int i = 0; i < list.size(); i++)
        {
            board[i] = new Pair[list.get(i).size()];
            for (int j = 0; j < list.get(i).size(); j++)
                board[i][j] = list.get(i).get(j);
        }

        for (int i = 0; i < list.size(); i++)
        {
            for (int j = 0; j < list.get(i).size(); j++)
            {
                assertEquals(board[i][j].getColor(), original[i][j].getColor());
                assertEquals(board[i][j].getValue(), original[i][j].getValue());
            }
        }
        assertEquals(recived, user);
    }

    @Test
    void encodeAndDecodeStringInteger() throws JSONException
    {
        String[] originalUser = new String[]{"KIMOSABE", "LONE RANGER", "THE BEARD"};
        int[] originalPoints = new int[]{15, 4, 87};

        JSONArray arr = JSONFacilities.encodeStringInteger(originalUser, originalPoints);
        StringBuilder result = new StringBuilder(arr.toString());
        result.append("\n");

        String[][] recived = JSONFacilities.decodeStringInteger(result.toString());

        for (int i = 0; i < recived.length; i++)
        {
            assertEquals(originalUser[i], recived[i][0]);
            assertEquals(originalPoints[i], Integer.parseInt(recived[i][1]));
        }
    }

    @Test
    void encodeAndDecodeMove() throws JSONException
    {
        Pair origianlPair = new Pair(3, ColorEnum.GREEN);
        Coordinates originalCoord = new Coordinates(4, 2);
        JSONArray json = JSONFacilities.encodeMove(originalCoord, origianlPair);

        StringBuilder move = new StringBuilder(json.toString());
        move.append("\n");

        ArrayList arr = JSONFacilities.decodeMove(move.toString());

        Pair pair = (Pair) arr.get(0);
        Coordinates coord = (Coordinates) arr.get(1);

        assertEquals(coord.getI(), originalCoord.getI());
        assertEquals(coord.getJ(), originalCoord.getJ());
        assertEquals(origianlPair.getValue(), pair.getValue());
        assertEquals(origianlPair.getColor(), pair.getColor());
    }

    @Test
    void encodeAndDecodeTool0() throws JSONException
    {
        Pair origianlPair = new Pair(3, ColorEnum.GREEN);
        String originalString = "helo";

        for (int i = 0; i < 3; i++)
        {
            JSONArray json = JSONFacilities.encodeTool(origianlPair, originalString);
            StringBuilder move = new StringBuilder(json.toString());
            move.append("\n");

            ArrayList arr = JSONFacilities.decodeTool(0, move.toString());

            Pair pair = (Pair) ((Wrapper) arr.get(0)).getParam();
            String str = (String) ((Wrapper) arr.get(1)).getParam();

            if (origianlPair == null)
                assertNull(pair);
            else
            {
                assertEquals(pair.getColor(), origianlPair.getColor());
                assertEquals(pair.getValue(), origianlPair.getValue());
            }
            assertEquals(str, originalString);

            if (i == 0)
                origianlPair.setColor(null);
            if (i == 1)
                origianlPair = null;
            else if (i == 2)
                originalString = null;
        }

    }

    @Test
    void encodeAndDecodeTool1() throws JSONException
    {
        Coordinates origianlCoord1 = new Coordinates(3, 5);
        Coordinates origianlCoord2 = new Coordinates(2, 0);

        for (int i = 0; i < 4; i++)
        {
            JSONArray json = JSONFacilities.encodeTool(origianlCoord1, origianlCoord2);
            StringBuilder move = new StringBuilder(json.toString());
            move.append("\n");

            ArrayList arr = JSONFacilities.decodeTool(1, move.toString());

            Coordinates coord1 = (Coordinates) ((Wrapper) arr.get(0)).getParam();
            Coordinates coord2 = (Coordinates) ((Wrapper) arr.get(1)).getParam();

            assertEquals(origianlCoord1.getI(), coord1.getI());
            assertEquals(origianlCoord1.getJ(), coord1.getJ());
            assertEquals(origianlCoord2.getI(), coord2.getI());
            assertEquals(origianlCoord2.getJ(), coord2.getJ());
        }
    }

    @Test
    void encodeAndDecodeTool2() throws JSONException
    {
        Pair orginalPair = null;
        Coordinates origianlCoord1 = new Coordinates(3, 5);
        Coordinates origianlCoord2 = new Coordinates(2, 0);
        Coordinates origianlCoord3 = new Coordinates(5, 2);
        Coordinates origianlCoord4 = new Coordinates(4, 1);

        for (int i = 0; i < 3; i++)
        {
            JSONArray json = JSONFacilities.encodeTool(orginalPair, origianlCoord1, origianlCoord2, origianlCoord3, origianlCoord4);
            StringBuilder move = new StringBuilder(json.toString());
            move.append("\n");

            ArrayList arr = JSONFacilities.decodeTool(2, move.toString());

            Pair pair = (Pair) ((Wrapper) arr.get(0)).getParam();
            Coordinates coord1 = (Coordinates) ((Wrapper) arr.get(1)).getParam();
            Coordinates coord2 = (Coordinates) ((Wrapper) arr.get(2)).getParam();
            Coordinates coord3 = (Coordinates) ((Wrapper) arr.get(3)).getParam();
            Coordinates coord4 = (Coordinates) ((Wrapper) arr.get(4)).getParam();

            if (orginalPair == null)
                assertNull(pair);
            else
            {
                assertEquals(orginalPair.getValue(), pair.getValue());
                assertEquals(orginalPair.getColor(), pair.getColor());
            }

            if (origianlCoord3 == null && origianlCoord4 == null)
            {
                assertNull(coord3);
                assertNull(coord4);
            } else
            {
                assertEquals(origianlCoord3.getI(), coord3.getI());
                assertEquals(origianlCoord3.getI(), coord3.getI());
                assertEquals(origianlCoord4.getJ(), coord4.getJ());
                assertEquals(origianlCoord4.getJ(), coord4.getJ());
            }
            assertEquals(origianlCoord1.getI(), coord1.getI());
            assertEquals(origianlCoord1.getJ(), coord1.getJ());
            assertEquals(origianlCoord2.getI(), coord2.getI());
            assertEquals(origianlCoord2.getJ(), coord2.getJ());


            if (i == 0)
                orginalPair = new Pair(3, ColorEnum.GREEN);
            else if (i == 1)
            {
                origianlCoord3 = null;
                origianlCoord4 = null;
            }
        }
    }

    @Test
    void encodeAndDecodeTool3() throws JSONException
    {
        Pair orginalPair1 = new Pair(3, ColorEnum.GREEN);
        Pair orginalPair2 = new Pair(5, ColorEnum.RED);
        int orifginalPosition = 3;
        JSONArray json = JSONFacilities.encodeTool(orginalPair1, orginalPair2, orifginalPosition);

        StringBuilder move = new StringBuilder(json.toString());
        move.append("\n");

        ArrayList arr = JSONFacilities.decodeTool(3, move.toString());

        Pair pair1 = (Pair) ((Wrapper) arr.get(0)).getParam();
        Pair pair2 = (Pair) ((Wrapper) arr.get(1)).getParam();
        int position = (Integer) ((Wrapper) arr.get(2)).getParam();

        assertEquals(orginalPair1.getValue(), pair1.getValue());
        assertEquals(orginalPair1.getColor(), pair1.getColor());
        assertEquals(orginalPair2.getValue(), pair2.getValue());
        assertEquals(orginalPair2.getColor(), pair2.getColor());
        assertEquals(orifginalPosition,position);
    }

    @Test
    void encodeAndDecodeTool4() throws JSONException
    {
        JSONArray json = JSONFacilities.encodeTool();

        StringBuilder move = new StringBuilder(json.toString());
        move.append("\n");

        ArrayList arr = JSONFacilities.decodeTool(4, move.toString());

        assertEquals(arr.size(),0);
    }
}