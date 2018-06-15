package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONFacilitiesTest
{
    Window w;
    RoundTrace t;
    @org.junit.jupiter.api.BeforeEach
    void setUp() throws ParserXMLException,IllegalDiceException
    {
        w = new Window("resources/vetrate/xml/kaleidoscopic_dream.xml");
        w.addDice(0,0,new Dice(1, ColorEnum.YELLOW),0);
        w.addDice(0,1,new Dice(5,ColorEnum.BLUE),0);

        t = new RoundTrace();
        t.addDice(1,new Dice(1, ColorEnum.YELLOW));
        t.addDice(1,new Dice(2, ColorEnum.RED));
        t.addDice(2,new Dice(1, ColorEnum.BLUE));
    }

    @Test
    void encodeAndDecodeMatrixPair() throws ParserXMLException, IllegalDiceException, JSONException {
        Pair[][] original = w.getPairMatrix();
        JSONArray arr = JSONFacilities.encodeMatrixPair(original);
        StringBuilder windows = new StringBuilder(arr.toString());
        windows.append("\n");

        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(windows.toString());

        Pair[][] board = new Pair[list.size()][];
        for (int i = 0 ; i < list.size() ; i++)
        {
            board[i] = new Pair[list.get(i).size()];
            for (int j = 0 ; j < list.get(i).size() ; j++)
                board[i][j] = list.get(i).get(j);
        }

        for (int i = 0 ; i < list.size() ; i++)
        {
            for (int j = 0 ; j < list.get(i).size() ; j++)
            {
                assertEquals (board[i][j].getColor(),original[i][j].getColor());
                assertEquals (board[i][j].getValue(),original[i][j].getValue());
            }
        }
    }


    @Test
    void encodeAndDecodeListArrayPair() throws ParserXMLException, IllegalDiceException, JSONException {
        ArrayList<Pair>[] original = t.getPair();
        JSONArray arr = JSONFacilities.encodeMatrixPair(original);
        StringBuilder trace = new StringBuilder(arr.toString());
        trace.append("\n");

        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPair(trace.toString());

        ArrayList<Pair>[] round = new ArrayList[list.size()];
        for (int i = 0 ; i < list.size() ; i++)
            round[i] = list.get(i);

        for (int i = 0 ; i < round.length ; i++)
        {
            for (int j = 0 ; j < round[i].size() ; j++)
            {
                assertEquals (round[i].get(j).getColor(),original[i].get(j).getColor());
                assertEquals (round[i].get(j).getValue(),original[i].get(j).getValue());
            }
        }
    }

    @Test
    void encodeAndDecodeNumber () throws JSONException
    {
        int original = 5;
        JSONObject obj = JSONFacilities.encodeInteger(original);
        StringBuilder trace = new StringBuilder(obj.toString());
        trace.append("\n");

        int decoded = JSONFacilities.decodeInteger(trace.toString());
        assertEquals(original,decoded);
    }

    @Test
    void encodeANdDecodeMatrixPairWithString ()
    {
        String user = "KIMOSABE";
        Pair[][] original = w.getPairMatrix();
        JSONArray arr = JSONFacilities.encodeMatrixPair(user,original);
        StringBuilder windows = new StringBuilder(arr.toString());
        windows.append("\n");

        String recived = JSONFacilities.decodeStringInMatrixPair(windows.toString());
        ArrayList<ArrayList<Pair>> list = JSONFacilities.decodeMatrixPairWithString(windows.toString());

        Pair[][] board = new Pair[list.size()][];
        for (int i = 0 ; i < list.size() ; i++)
        {
            board[i] = new Pair[list.get(i).size()];
            for (int j = 0 ; j < list.get(i).size() ; j++)
                board[i][j] = list.get(i).get(j);
        }

        for (int i = 0 ; i < list.size() ; i++)
        {
            for (int j = 0 ; j < list.get(i).size() ; j++)
            {
                assertEquals (board[i][j].getColor(),original[i][j].getColor());
                assertEquals (board[i][j].getValue(),original[i][j].getValue());
            }
        }
        assertEquals(recived,user);
    }
}