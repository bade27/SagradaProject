package Test.Model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CellTest
{
    static Cell backColorVoid;
    static Cell backValueVoid;
    static Cell backVoid;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        backColorVoid = new Cell(new Placement(3, null));
        backValueVoid = new Cell(new Placement(0,Color.BLUE ));
        backVoid = new Cell(new Placement(0, null));

        assertNull(backColorVoid.getFrontDice());
        assertNull(backValueVoid.getFrontDice());
        assertNull(backVoid.getFrontDice());
    }

    @Test
    void setDiceTest () {
        assertTrue(backColorVoid.setDice(new Dice(3,Color.RED)));
        assertFalse(backColorVoid.setDice(new Dice(5,Color.RED)));

        assertTrue(backValueVoid.setDice(new Dice(5,Color.BLUE)));
        assertFalse(backValueVoid.setDice(new Dice(3,Color.RED)));

        assertTrue(backVoid.setDice(new Dice(3,Color.RED)));
        assertTrue(backVoid.setDice(new Dice(5,Color.BLUE)));
    }




}