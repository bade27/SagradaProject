package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.IllegalDiceException;
import it.polimi.ingsw.Exceptions.ParserXMLException;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;


/*
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
    void firstDiceCheck()
    {
        //Check first die position
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 3, new Dice(3, Color.RED), 0); });
        assertEquals(e1.getMessage(), "First die not placed on edge");
        try {
            w.addDice(0, 3, new Dice(3, Color.RED), 0);
        }
        catch (IllegalDiceException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    @Test
    void positionOutOfBoundCheck ()
    {
        //Check position index
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(4, 4, new Dice(3, Color.RED), 0); });
        assertEquals(e1.getMessage(), "Position out of bound");
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 5, new Dice(3, Color.RED), 0); });
        assertEquals(e2.getMessage(), "Position out of bound");
    }

    /*
            p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | p:1-n/d    |
            p:0-Green  | p:0-n/d    | p:5-n/d    | p:0-n/d    | p:4-n/d    |
            p:3-n/d    | p:0-n/d    | p:0-Red    | p:0-n/d    | p:0-Green  |
            p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
    */

    @Test
    void placmentCheck () {
        firstDiceCheck();
        //Check overlapped dice
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 3, new Dice(2, Color.GREEN), 0); });
        assertEquals(e1.getMessage(), "Die placed on another one");

        //Check positioning
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(3, Color.GREEN), 0); });
        assertEquals(e2.getMessage(), "Die not placed near a compatible one");

        //Check positioning
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(4, Color.RED), 0); });
        assertEquals(e3.getMessage(), "Die not placed near a compatible one");

        //Check Placement compatible
        IllegalDiceException e4 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(4, Color.GREEN), 0); });
        assertEquals(e4.getMessage(), "Die not placed on compatible cell");
    }

    @Test
    void massiveFilling ()
    {
        placmentCheck();
        try {
            //Diagonal positioning accepted
            w.addDice(1,2,new Dice (5, Color.RED),0);
            w.addDice(1,4,new Dice (4, Color.RED),0);
            /*
                p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | p:1-n/d    |
                p:0-Green  | p:0-n/d    | D:5-Red    | p:0-n/d    | D:4-Red    |
                p:3-n/d    | p:0-n/d    | p:0-Red    | p:0-n/d    | p:0-Green  |
                p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
            */
        }
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }

        //Vertical/Horizontal positioning not accepted
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 3, new Dice(5, Color.GREEN), 0); });
        assertEquals(e1.getMessage(), "Die not placed near a compatible one");

        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 3, new Dice(2, Color.RED), 0); });
        assertEquals(e2.getMessage(), "Die not placed near a compatible one");

        try {
            //Vertical/Horizontal positioning accepted
            w.addDice(1,3,new Dice (2, Color.GREEN),0);
            w.addDice(2,3,new Dice (3, Color.BLUE),0);

            /*
                p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | p:1-n/d    |
                p:0-Green  | p:0-n/d    | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | p:0-n/d    | p:0-Red    | D:3-Blue   | p:0-Green  |
                p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
             */

            //Massive positioning accepted
            w.addDice(0,4,new Dice (1, Color.BLUE),0);
            w.addDice(2,4,new Dice (5, Color.GREEN),0);
            w.addDice(3,4,new Dice (1, Color.YELLOW),0);

            /*
                p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | D:1-Blue   |
                p:0-Green  | p:0-n/d    | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | p:0-n/d    | p:0-Red    | D:3-Blue   | D:5-Green  |
                p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | D:1-Yellow |
             */

            w.addDice(0,2,new Dice (1, Color.YELLOW),0);
            w.addDice(3,2,new Dice (3, Color.BLUE),0);
            w.addDice(0,1,new Dice (3, Color.BLUE),0);
            w.addDice(1,1,new Dice (6, Color.GREEN),0);
            w.addDice(2,1,new Dice (4, Color.YELLOW),0);
            w.addDice(3,1,new Dice (2, Color.GREEN),0);

            /*
                p:0-Yellow | D:3-Blue   | D:1-Yellow | D:3-Red    | D:1-Blue   |
                p:0-Green  | D:6-Green  | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | D:4-Yellow | p:0-Red    | D:3-Blue   | D:5-Green  |
                p:2-n/d    | D:2-Green  | D:3-Blue   | p:0-Blue   | D:1-Yellow |
             */

            w.addDice(0,0,new Dice (1, Color.YELLOW),0);
            w.addDice(2,0,new Dice (3, Color.MAGENTA),0);
        }

        /*
            D:1-Yellow | D:3-Blue   | D:1-Yellow | D:3-Red    | D:1-Blue   |
            p:0-Green  | D:6-Green  | D:5-Red    | D:2-Green  | D:4-Red    |
            D:3-Violet | D:4-Yellow | p:0-Red    | D:3-Blue   | D:5-Green  |
            p:2-n/d    | D:2-Green  | D:3-Blue   | p:0-Blue   | D:1-Yellow |
         */
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }

        //Dice not possible to place cause restriction: 3,3 2,2 1,0 3,0

        //Color Restriction (3,3):
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 3, new Dice(2, Color.BLUE), 0); });
        assertEquals(e3.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e4 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 3, new Dice(2, Color.MAGENTA), 0); });
        assertEquals(e4.getMessage(), "Die not placed on compatible cell");

        //Number Restriction (3,0):
        IllegalDiceException e5 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 0, new Dice(2, Color.BLUE), 0); });
        assertEquals(e5.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e6 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 0, new Dice(5, Color.RED), 0); });
        assertEquals(e6.getMessage(), "Die not placed on compatible cell");
    }
}