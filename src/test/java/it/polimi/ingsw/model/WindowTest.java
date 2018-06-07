package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.remoteInterface.Coordinates;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.util.Random;

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
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 3, new Dice(3, ColorEnum.RED), 0); });
        assertEquals(e1.getMessage(), "First die not placed on edge");
        try {
            w.addDice(0, 3, new Dice(3, ColorEnum.RED), 0);
        }
        catch (IllegalDiceException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    @Test
    void positionOutOfBoundCheck ()
    {
        //Check position index
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(4, 4, new Dice(3, ColorEnum.RED), 0); });
        assertEquals(e1.getMessage(), "Position out of bound");
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 5, new Dice(3, ColorEnum.RED), 0); });
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
        //System.out.println(w.toString());
        //Check overlapped die
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 3, new Dice(2, ColorEnum.GREEN), 0); });
        assertEquals(e1.getMessage(), "Die placed on another one");

        //Check positioning
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(3, ColorEnum.GREEN), 0); });
        assertEquals(e2.getMessage(), "Die not placed near a compatible one");

        //Check positioning
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(4, ColorEnum.RED), 0); });
        assertEquals(e3.getMessage(), "Die not placed near a compatible one");

        //Check Placement compatible
        IllegalDiceException e4 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 4, new Dice(4, ColorEnum.GREEN), 0); });
        assertEquals(e4.getMessage(), "Die not placed on compatible cell");
    }

    @Test
    void massiveFilling ()
    {
        placmentCheck();
        try {
            //Diagonal positioning accepted
            w.addDice(1,2,new Dice (5, ColorEnum.RED),0);
            w.addDice(1,4,new Dice (4, ColorEnum.RED),0);
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
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 3, new Dice(5, ColorEnum.GREEN), 0); });
        assertEquals(e1.getMessage(), "Die not placed near a compatible one");

        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 3, new Dice(2, ColorEnum.RED), 0); });
        assertEquals(e2.getMessage(), "Die not placed near a compatible one");

        try {
            //Vertical/Horizontal positioning accepted
            w.addDice(1,3,new Dice (2, ColorEnum.GREEN),0);
            w.addDice(2,3,new Dice (3, ColorEnum.BLUE),0);

            /*
                p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | p:1-n/d    |
                p:0-Green  | p:0-n/d    | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | p:0-n/d    | p:0-Red    | D:3-Blue   | p:0-Green  |
                p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
             */

            //Massive positioning accepted
            w.addDice(0,4,new Dice (1, ColorEnum.BLUE),0);
            w.addDice(2,4,new Dice (5, ColorEnum.GREEN),0);
            w.addDice(3,4,new Dice (1, ColorEnum.YELLOW),0);

            /*
                p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | D:1-Blue   |
                p:0-Green  | p:0-n/d    | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | p:0-n/d    | p:0-Red    | D:3-Blue   | D:5-Green  |
                p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | D:1-Yellow |
             */

            w.addDice(0,2,new Dice (1, ColorEnum.YELLOW),0);
            w.addDice(3,2,new Dice (3, ColorEnum.BLUE),0);
            w.addDice(0,1,new Dice (3, ColorEnum.BLUE),0);
            w.addDice(1,1,new Dice (6, ColorEnum.GREEN),0);
            w.addDice(2,1,new Dice (4, ColorEnum.YELLOW),0);
            w.addDice(3,1,new Dice (2, ColorEnum.GREEN),0);

            /*
                p:0-Yellow | D:3-Blue   | D:1-Yellow | D:3-Red    | D:1-Blue   |
                p:0-Green  | D:6-Green  | D:5-Red    | D:2-Green  | D:4-Red    |
                p:3-n/d    | D:4-Yellow | p:0-Red    | D:3-Blue   | D:5-Green  |
                p:2-n/d    | D:2-Green  | D:3-Blue   | p:0-Blue   | D:1-Yellow |
             */

            w.addDice(0,0,new Dice (1, ColorEnum.YELLOW),0);
            w.addDice(2,0,new Dice (3, ColorEnum.PURPLE),0);
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

        //ColorEnum Restriction (3,3):
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 3, new Dice(2, ColorEnum.BLUE), 0); });
        assertEquals(e3.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e4 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 3, new Dice(2, ColorEnum.PURPLE), 0); });
        assertEquals(e4.getMessage(), "Die not placed on compatible cell");

        //Number Restriction (3,0):
        IllegalDiceException e5 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 0, new Dice(2, ColorEnum.BLUE), 0); });
        assertEquals(e5.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e6 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 0, new Dice(5, ColorEnum.RED), 0); });
        assertEquals(e6.getMessage(), "Die not placed on compatible cell");
    }

    @Test
    void emptyCell() throws IllegalDiceException {
        int i=new Random().nextInt(4);
        int j=new Random().nextInt(5);

        //creazione dado casuale
        Random col = new Random();
        Random num = new Random();
        int number = num.nextInt(6) + 1;
        ColorEnum color = null;
        switch (col.nextInt(5)) {
            case 0:
                color = ColorEnum.RED;
                break;
            case 1:
                color = ColorEnum.GREEN;
                break;
            case 2:
                color = ColorEnum.BLUE;
                break;
            case 3:
                color = ColorEnum.YELLOW;
                break;
            case 4:
                color = ColorEnum.PURPLE;
                break;

            default:
                break;
        }
        Dice d = new Dice(number, color);
        w.addDice(i,j,d,-1);

        Dice d1=w.getCell(i,j).getFrontDice();;
        assertNotNull(d1);

        w.emptyCell(i,j);

        d1=w.getCell(i,j).getFrontDice();
        assertNull(d1);

        w.emptyCell(i,j);

        d1=w.getCell(i,j).getFrontDice();
        assertNull(d1);
    }

    @Test()
    void moveDicenocontrol() throws IllegalDiceException {
        int value=new Random().nextInt(6)+1;
        ColorEnum color=null;
        int col = new Random().nextInt(5);
        switch (col) {
            case 0:
                color = ColorEnum.RED;
                break;
            case 1:
                color = ColorEnum.GREEN;
                break;
            case 2:
                color = ColorEnum.BLUE;
                break;
            case 3:
                color = ColorEnum.YELLOW;
                break;
            case 4:
                color = ColorEnum.PURPLE;
                break;

            default:
                break;
        }
        Dice d = new Dice(value, color);
        int i1=new Random().nextInt(4);
        int j1=new Random().nextInt(5);

        while(i1!=0&&i1!=4&&j1!=0&&j1!=3) {
            i1 = new Random().nextInt(4);
            j1 = new Random().nextInt(5);
        }
        Integer [] pos_in={i1,j1};
        w.addDice(i1,j1,d,-1);

        int i2=new Random().nextInt(4);
        int j2=new Random().nextInt(5);

        while(i2!=0&&i2!=4&&j2!=0&&j2!=3) {
            i2 = new Random().nextInt(4);
            j2 = new Random().nextInt(5);
        }
        Integer [] pos_fin={i2,j2};

        Dice di1=w.getCell(i1,j1).getFrontDice();
        Dice di2=w.getCell(i2,j2).getFrontDice();

        w.moveDice(new Coordinates(pos_in[0],pos_in[1]),new Coordinates(pos_fin[0],pos_fin[1]),-1);

        Dice df1=w.getCell(i1,j1).getFrontDice();
        Dice df2=w.getCell(i2,j2).getFrontDice();

        if(i1!=i2||j1!=j2){
            assertEquals(di1,d);
            assertNull(di2);
            assertEquals(df2,d);
            assertNull(df1);
        }else{
            assertEquals(di1,d);
            assertEquals(df1,d);
        }
    }

    @Test
    void passedControlTest ()
    {
        placmentCheck();

        //Control id 2 (Control to placement ColorEnum)
        try {
            //Control id 2 accepted
            w.addDice(0,4,new Dice (5, ColorEnum.BLUE),2);
            w.addDice(1,4,new Dice (6, ColorEnum.GREEN),2);

            /*
             p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | D:5-Blue   |
             p:0-Green  | p:0-n/d    | p:5-n/d    | p:0-n/d    | D:6-Green  |
             p:3-n/d    | p:0-n/d    | p:0-Red    | p:0-n/d    | p:0-Green  |
             p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | p:0-Yellow |
            */
        }
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }
        //System.out.println(w.toString());
        //Control id 2 rejected
        IllegalDiceException e1 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 4, new Dice(5, ColorEnum.RED), 2); });
        assertEquals(e1.getMessage(), "Die not placed on compatible cell");
        IllegalDiceException e3 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 3, new Dice(5, ColorEnum.RED), 2); });
        assertEquals(e3.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e2 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 0, new Dice(5, ColorEnum.RED), 2); });
        assertEquals(e2.getMessage(), "Die not placed near a compatible one");

        //Control id 1 (Control to placement Value)
        try {
            //Control id 1 accepted
            w.addDice(2,4,new Dice (5, ColorEnum.RED),1);
            w.addDice(2,3,new Dice (6, ColorEnum.GREEN),1);
            w.addDice(3,4,new Dice (3, ColorEnum.BLUE),1);
            /*
              p:0-Yellow | p:0-Blue   | p:0-n/d    | D:3-Red    | D:5-Blue   |
              p:0-Green  | p:0-n/d    | p:5-n/d    | p:0-n/d    | D:6-Green  |
              p:3-n/d    | p:0-n/d    | p:0-Red    | D:6-Green  | D:5-Red    |
              p:2-n/d    | p:0-n/d    | p:0-n/d    | p:0-Blue   | D:3-Blue   |
            */
        }
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }
        //System.out.println(w.toString());
        //Control id 1 rejected
        IllegalDiceException e4 = assertThrows(IllegalDiceException.class, () -> { w.addDice(1, 2, new Dice(3, ColorEnum.RED), 1); });
        assertEquals(e4.getMessage(), "Die not placed on compatible cell");
        IllegalDiceException e5 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 2, new Dice(5, ColorEnum.RED), 1); });
        assertEquals(e5.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e6 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 0, new Dice(3, ColorEnum.RED), 1); });
        assertEquals(e6.getMessage(), "Die not placed near a compatible one");

        //Control id 3 (Control to no near die)
        try {
            //Control id 3 accepted
            w.addDice(3,1,new Dice (3, ColorEnum.RED),3);
            w.addDice(2,0,new Dice (3, ColorEnum.GREEN),3);
            w.addDice(0,2,new Dice (4, ColorEnum.BLUE),3);
            /*
               p:0-Yellow | p:0-Blue   | D:4-Blue   | D:3-Red    | D:5-Blue   |
               p:0-Green  | p:0-n/d    | p:5-n/d    | p:0-n/d    | D:6-Green  |
               D:3-Green  | p:0-n/d    | p:0-Red    | D:6-Green  | D:5-Red    |
               p:2-n/d    | D:3-Red    | p:0-n/d    | p:0-Blue   | D:3-Blue   |
            */
        }
        catch (IllegalDiceException e){
            throw new Error(e.getMessage(),e);
        }
        //System.out.println(w.toString());
        //Control id 3 rejected
        IllegalDiceException e7 = assertThrows(IllegalDiceException.class, () -> { w.addDice(0, 0, new Dice(3, ColorEnum.RED), 3); });
        assertEquals(e7.getMessage(), "Die not placed on compatible cell");
        IllegalDiceException e8 = assertThrows(IllegalDiceException.class, () -> { w.addDice(2, 1, new Dice(5, ColorEnum.GREEN), 3); });
        assertEquals(e8.getMessage(), "Die not placed near a compatible one");
        IllegalDiceException e9 = assertThrows(IllegalDiceException.class, () -> { w.addDice(3, 2, new Dice(3, ColorEnum.RED), 3); });
        assertEquals(e9.getMessage(), "Die not placed near a compatible one");
    }
}