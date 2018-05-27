package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PlacementTest {

    static Placement place;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        place = new Placement(3, ColorEnum.BLUE);
    }

    @Test
    void isEqualTrue () {
        assertTrue(place.isEqual(new Dice (3,ColorEnum.BLUE)));
    }

    @Test
    void isEqualValueFalse () {
        assertFalse(place.isEqual(new Dice (5,ColorEnum.BLUE)));
    }

    @Test
    void isEqualColorFalse () {
        assertFalse(place.isEqual(new Dice (3,ColorEnum.RED)));
    }

    @Test
    void isSimilarValueColorTrue () {
        assertTrue(place.isSimilar(new Dice (3,ColorEnum.BLUE)));
    }

    @Test
    void isSimilarValueTrue () {
        assertTrue(place.isSimilar(new Dice (3,ColorEnum.RED)));
    }

    @Test
    void isSimilarColorTrue () {
        assertTrue(place.isSimilar(new Dice (5,ColorEnum.BLUE)));
    }

    @Test
    void isSimilarColorFalse () {
        assertFalse(place.isSimilar(new Dice (5,ColorEnum.RED)));
    }

    @Test
    void isColorEquals() { assertTrue(place.isColorEquals(new Dice (5,ColorEnum.BLUE))); assertFalse(place.isColorEquals(new Dice (5,ColorEnum.RED)));}

    @Test
    void isValueEquals() { assertTrue(place.isValueEquals(new Dice (3,ColorEnum.RED))); assertFalse(place.isValueEquals(new Dice (5,ColorEnum.BLUE)));}


    @Test
    void equalCOLORcolor (){
        assertTrue(Color.red == Color.RED);
    }

    @Test
    void notequalCOLORcolor (){
        assertFalse(Color.blue == Color.RED);
    }

    @Test
    void toStringTest() {
        assertNotNull(place.toString());
    }
}