package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    static Dice d;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        d = new Dice(3, ColorEnum.BLUE);
    }

    @Test
    void isEqualTrue () {
        assertTrue(d.isEqual(new Dice (3,ColorEnum.BLUE)));
    }

    @Test
    void isEqualValueFalse () {
        assertFalse(d.isEqual(new Dice (5,ColorEnum.BLUE)));
    }

    @Test
    void isEqualColorFalse () {
        assertFalse(d.isEqual(new Dice (3,ColorEnum.RED)));
    }

    @Test
    void isSimilarValueColorTrue () {
        assertTrue(d.isSimilar(new Dice (3,ColorEnum.BLUE)));
    }

    @Test
    void isSimilarValueTrue () {
        assertTrue(d.isSimilar(new Dice (3,ColorEnum.RED)));
    }

    @Test
    void isSimilarColorTrue () {
        assertTrue(d.isSimilar(new Dice (5,ColorEnum.BLUE)));
    }

    @Test
    void isSimilarFalse () {
        assertFalse(d.isSimilar(new Dice (5,ColorEnum.RED)));
    }

    @Test
    void cloneDice() {
        assertTrue(d.isEqual(d.cloneDice()));
    }

    @Test
    void toStringTest() {
        assertNotNull(d.toString());
    }

}