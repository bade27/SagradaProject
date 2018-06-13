package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.Pair;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JSONFacilitiesTest
{
    Window w;
    @org.junit.jupiter.api.BeforeEach
    void setUp() throws ParserXMLException,IllegalDiceException
    {
        w = new Window("resources/vetrate/xml/kaleidoscopic_dream.xml");
        w.addDice(0,0,new Dice(1, ColorEnum.YELLOW),0);
        w.addDice(0,1,new Dice(5,ColorEnum.BLUE),0);
    }

    @Test
    void encodeAndDecodeMatrixPair() throws ParserXMLException,IllegalDiceException
    {
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
}