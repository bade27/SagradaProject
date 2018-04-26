package Test.Model;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    static Dice d;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        d = new Dice(5, new Color(255, 230, 152));
    }

    @Test
    void isEqualTrue() {
        Dice d1 = new Dice(5, new Color(255, 230, 152));
        assertTrue(d1.isEqual(d));
    }

    @Test
    void isEqualFasle() {
        Dice d1 = new Dice(3, new Color(255, 230, 152));
        assertFalse(d1.isEqual(d));
    }

    @Test
    void isSimilarTrue() {
        Dice d1 = new Dice(3, new Color(255, 230, 152));
        assertTrue(d1.isSimilar(d));
    }

    @Test
    void isSimilarFalse() {
        Dice d1 = new Dice(1, new Color(15, 230, 152));
        assertFalse(d1.isSimilar(d));
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