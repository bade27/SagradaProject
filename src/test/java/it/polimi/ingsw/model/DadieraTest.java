package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class DadieraTest {

    private Dadiera d;
    private int n;
    private int expectedDice;

    @BeforeEach
    void setUp() {
        n = new Random().nextInt(9) + 1;
        d = new Dadiera();
        expectedDice = 2*n + 1;
    }

    @Test
    void mix() {
        d.mix(n);
        assertNotNull(d.getListaDadi());
        assertEquals(expectedDice, d.getListaDadi().size());
    }

    @Test
    void deleteDice() {
        d.mix(n);
        int which_Die = new Random().nextInt(expectedDice);
        ArrayList<Dice> playableDice = d.getListaDadi();

        //lunghezza di partenza
        int oldLen = playableDice.size();

        //estrazione di un dado a caso
        Dice dice = playableDice.get(which_Die);

        //funzione per il conto dei dadi uguali a quello selezionato
        BiFunction<ArrayList<Dice>, Dice, Integer> counter = (list, d) -> {
            Long t = list.stream()
                    .filter(currentDice -> currentDice.isEqual(d))
                    .count();
            return t.intValue();
        };

        //numero dei dadi uguali a quello selezionato
        int oldEquals = counter.apply(playableDice, dice);

        //rimozione del dado selezionato
        d.deleteDice(dice);

        //numero dei dadi uguali rimasti uguali a quello rimosso
        int newEquals = counter.apply(playableDice, dice);

        assertEquals(oldEquals - 1, newEquals);
        assertEquals(oldLen - 1, playableDice.size());
    }

    @Test
    void getDiceWithException() throws IllegalDiceException {
        int k = 3;
        assertThrows(IllegalDiceException.class, () -> d.getDice(k));
    }

    @Test
    void getDiceNoException() throws IllegalDiceException {
        d.mix(n);
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
        d.mix(n);
        assertNotNull(d.toString());
        assertNotEquals("Dadiera vuota!", d.toString());
    }
}