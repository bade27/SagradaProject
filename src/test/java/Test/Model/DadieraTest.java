package Test.Model;

import Test.Exceptions.IllegalDiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DadieraTest {

    private Dadiera d;
    private int n;
    private int expectedDice;

    @BeforeEach
    void setUp() {
        n = new Random().nextInt(9) + 1;
        d = new Dadiera(n);
        expectedDice = 2*n + 1;
    }

    @Test
    void mix() {
        d.mix();
        assertNotNull(d.getListaDadi());
        assertEquals(expectedDice, d.getListaDadi().size());
    }

    @Test
    void deleteDice() {
        d.mix();
        int which_Die = new Random().nextInt(expectedDice);
        ArrayList<Dice> playableDice = d.getListaDadi();
        int oldLen = playableDice.size();
        Dice dice = playableDice.get(which_Die);
        d.deleteDice(dice);
        assertTrue(!playableDice.contains(dice));
        assertEquals(oldLen - 1, playableDice.size());
    }

    @Test
    void getDiceWithException() throws IllegalDiceException {
        int k = 3;
        assertThrows(IllegalDiceException.class, () -> d.getDice(k));
    }

    @Test
    void getDiceNoException() throws IllegalDiceException {
        d.mix();
        int which_Die = new Random().nextInt(expectedDice);
        assertNotNull(d.getDice(which_Die));
        assertEquals(d.getListaDadi().get(which_Die), d.getDice(which_Die));
    }

    @Test
    void toStringEmptyTest() {
        assertNotNull(d.toString());
        assertEquals("Dadiera vuota!", d.toString());
    }

    @Test
    void toStringNnEmptyTest() {
        d.mix();
        assertNotNull(d.toString());
        assertNotEquals("Dadiera vuota!", d.toString());
    }
}