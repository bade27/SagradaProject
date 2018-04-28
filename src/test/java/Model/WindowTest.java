package Model;

import Exceptions.IllegalDiceException;
import Exceptions.ParserXMLException;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;


/*
- test che verifica se un dado si puó mettere in una posizione secondo le regole di posizionamento
- test che verifica che un dado NON si puó mettere in una posizione occupata
- test che verifica che un dado NON si puó mettere in una posizione secondo le regole di posizionamento

- test che verifica se un dado si puó mettere in una posizione secondo le regole di posizionamento DOPO
la rimozione di un dado e.g. per simulare un dado spostato in altra posizione da carta strumento.

- test massivo (che riempie tutta la window)
 */


class WindowTest
{
    Window w;

    @org.junit.jupiter.api.BeforeEach
    void setUp()
    {
        assertThrows(ParserXMLException.class, () -> { w = new Window("test.xml"); });
        try {
            w = new Window("resources/vetrate/xml/kaleidoscopic_dream.xml");
        }
        catch (ParserXMLException ex){
            throw new TestAbortedException();
        }
        Cell[][] grid = w.getGrid();
        assertNotNull(grid);

        for (int i = 0; i < w.rows;i++)
            for (int j=0;j < w.cols ; j++)
            {
                assertNotNull(grid[i][j]);
                assertNotNull(grid[i][j].getPlacement());
                assertNull(grid[i][j].getFrontDice());
            }
    }

    //Rows = 4
    //Cols = 5
    @Test
    void addDice()
    {
        //Check first die position
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2,3,new Dice (3, Color.RED),0); });
        assertEquals(e1.getMessage(),"First die not put on board");
        try {
            w.addDice(0,3,new Dice (3, Color.RED),0);
        }
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }

        //Check position index
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(4,4,new Dice (3, Color.RED),0); });
        assertEquals(e2.getMessage(),"Position out of bound");
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3,5,new Dice (3, Color.RED),0); });
        assertEquals(e3.getMessage(),"Position out of bound");

        /*
            p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | p:1-n/d    |
            p:0-Green  | p:0-n/d    | p:5-n/d    | p:0-n/d    | p:4-n/d    |
            p:3-n/d    | p:0-n/d    | p:0-Red    | p:0-n/d    | p:0-Green  |
            p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
         */




    }
}