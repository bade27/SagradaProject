package it.polimi.ingsw.model;

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
        backValueVoid = new Cell(new Placement(0,ColorEnum.BLUE ));
        backVoid = new Cell(new Placement(0, null));

        assertNull(backColorVoid.getFrontDice());
        assertNull(backValueVoid.getFrontDice());
        assertNull(backVoid.getFrontDice());
    }

    @Test
    void setDiceTest () {
        assertTrue(backColorVoid.setDice(new Dice(3,ColorEnum.RED)));
        assertFalse(backColorVoid.setDice(new Dice(5,ColorEnum.RED)));

        assertTrue(backValueVoid.setDice(new Dice(5,ColorEnum.BLUE)));
        assertFalse(backValueVoid.setDice(new Dice(3,ColorEnum.RED)));

        assertTrue(backVoid.setDice(new Dice(3,ColorEnum.RED)));
        assertTrue(backVoid.setDice(new Dice(5,ColorEnum.BLUE)));
    }


    @Test
    void setDiceByColor() {
        assertTrue(backColorVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));
        assertTrue(backColorVoid.setDiceByColor(new Dice(5,ColorEnum.BLUE)));

        assertTrue(backValueVoid.setDiceByColor(new Dice(5,ColorEnum.BLUE)));
        assertFalse(backValueVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));

        assertTrue(backVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));
        assertTrue(backVoid.setDiceByColor(new Dice(5,ColorEnum.BLUE)));
    }



    @Test
    void setDiceByValue() {
        assertTrue(backColorVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));
        assertFalse(backColorVoid.setDice(new Dice(5,ColorEnum.BLUE)));

        assertTrue(backValueVoid.setDiceByColor(new Dice(5,ColorEnum.BLUE)));
        assertFalse(backValueVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));

        assertTrue(backVoid.setDiceByColor(new Dice(3,ColorEnum.RED)));
        assertTrue(backVoid.setDiceByColor(new Dice(5,ColorEnum.BLUE)));
    }
}