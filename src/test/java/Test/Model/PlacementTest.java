package Test.Model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PlacementTest {

    static Placement place;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        place = new Placement(3, Color.BLUE);
    }

    @Test
    void isEqualTrue () {
        assertTrue(place.isEqual(new Dice (3,Color.BLUE)));
    }

    @Test
    void isEqualValueFalse () {
        assertFalse(place.isEqual(new Dice (5,Color.BLUE)));
    }

    @Test
    void isEqualColorFalse () {
        assertFalse(place.isEqual(new Dice (3,Color.RED)));
    }

    @Test
    void isSimilarValueColorTrue () {
        assertTrue(place.isSimilar(new Dice (3,Color.BLUE)));
    }

    @Test
    void isSimilarValueTrue () {
        assertTrue(place.isSimilar(new Dice (3,Color.RED)));
    }

    @Test
    void isSimilarColorTrue () {
        assertTrue(place.isSimilar(new Dice (5,Color.BLUE)));
    }

    @Test
    void isSimilarFalse () {
        assertFalse(place.isSimilar(new Dice (5,Color.RED)));
    }

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