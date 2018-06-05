package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MoveGridGridToolTest {
/*
    private Window window;
    private MoveGridGridTool mggt;

    @BeforeEach
    void setUp() {
        try {
            window = new Window("resources/vetrate/xml/kaleidoscopic_dream.xml");
        }catch (ParserXMLException ex) {
            throw new TestAbortedException();
        }
    }

    @Test
    void moveOneDieToollevel1() throws IllegalDiceException {
        mggt = new MoveGridGridTool(2, "Pennello per Eglomise");
        Random col=new Random();
        Random num=new Random();
        int value=num.nextInt(6)+1;
        ColorEnum color=null;
        switch (col.nextInt(5))
        {case 0:
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
        Dice dice=new Dice(value,color);

        Dice d = new Dice(value, color);
        int i1=new Random().nextInt(4);
        int j1=new Random().nextInt(5);

        while(i1!=0&&i1!=4&&j1!=0&&j1!=3) {
            i1 = new Random().nextInt(4);
            j1 = new Random().nextInt(5);
        }
        int [] pos_in={i1,j1};
        window.addDice(i1,j1,dice,-1);

        int i2=new Random().nextInt(4);
        int j2=new Random().nextInt(5);

        while(i2!=0&&i2!=4&&j2!=0&&j2!=3) {
            i2 = new Random().nextInt(4);
            j2 = new Random().nextInt(5);
        }
        int [] pos_fin={i2,j2};

        Dice di1=window.getCell(i1,j1).getFrontDice();
        Dice di2=window.getCell(i2,j2).getFrontDice();

    }

    @Test
    void moveOneDieToollevel2() {
        mggt = new MoveGridGridTool(2, "Pennello per Eglomise");
        Random col=new Random();
        Random num=new Random();
        int number=num.nextInt(6)+1;
        ColorEnum color=null;
        switch (col.nextInt(5))
        {case 0:
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
        Dice dice=new Dice(number,color);
    }

    @Test
    void moveTwoDieTool() {
    }

    @Test
    void setPrice() {
    }

    @Test
    void getPrice() {
    }

    @Test
    void canPlaceDie() {
    }
    */
}