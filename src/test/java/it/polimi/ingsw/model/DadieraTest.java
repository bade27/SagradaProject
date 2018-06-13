package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.NotEnoughDiceException;
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
        n = new Random().nextInt(4) + 1;
        d = new Dadiera();
        expectedDice = 2*n + 1;
    }

    @Test
    void mix() throws NotEnoughDiceException {
        d.mix(n);
        assertNotNull(d.getDiceList());
        assertEquals(expectedDice, d.getDiceList().size());
    }

    @Test()
    void deleteDice() throws NotEnoughDiceException {
        d.mix(n);
        int which_Die = new Random().nextInt(expectedDice);
        ArrayList<Dice> playableDice = d.getDiceList();

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

    @Test()
    void addDice() throws NotEnoughDiceException{
        d.mix(n);
        ArrayList<Dice> playableDice = d.getDiceList();

        //lunghezza dadiera iniziale
        int oldLen = playableDice.size();

        //creazione dado casuale
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

        // funzione dadi uguali a quello inserito
        BiFunction<ArrayList<Dice>, Dice, Integer> numDice = (list, d) -> {
            Long t = list.stream()
                    .filter(currentDice -> currentDice.isEqual(d))
                    .count();
            return t.intValue();
        };


        //numero dei dadi uguali a quello estratto
        int oldEquals = numDice.apply(playableDice, dice);

        //aggiunta dado
        d.addDice(dice);

        //numero dei dadi uguali dopo aver aggiunto il dado
        int newEquals = numDice.apply(playableDice, dice);

        //nuova lunghezza dadiera
        int newLen=d.getDiceList().size();

        assertEquals(oldLen,newLen-1);
        assertEquals(oldEquals,newEquals-1);
        assertNotNull(color);

    }

    @Test()
    void setDiceValueException() throws NotEnoughDiceException{
        d.mix(n);
        ArrayList<Dice> playableDice = d.getDiceList();
        int which_Die = new Random().nextInt(expectedDice);

        Random n=new Random();
        int a=7+Math.abs(n.nextInt());
        int b=0-Math.abs(n.nextInt());

        //estrazione di un dado a caso
        Dice dice = playableDice.get(which_Die);

        assertThrows(IllegalDiceException.class, () -> d.setDiceValue(a,dice));
        assertThrows(IllegalDiceException.class, () -> d.setDiceValue(b,dice));
    }

    @Test
    void setDiceValue() throws IllegalDiceException,NotEnoughDiceException{
        d.mix(n);
        ArrayList<Dice> playableDice = d.getDiceList();

        System.out.println();

        int which_Die = new Random().nextInt(expectedDice);

        //creo valore del dado casualmente
        Integer n;

        //estrazione di un dado da dadiera
        Dice dice = playableDice.get(which_Die);
        Dice oldDice = dice.cloneDice();

        do {
            n = new Random().nextInt(6)+1;
        } while(n == oldDice.getValue());


        long numBeforeSet = playableDice.stream().filter(d -> d.isEqual(oldDice)).count();
        d.setDiceValue(n, dice);

        long numAfterSet = playableDice.stream().filter(d -> d.isEqual(oldDice)).count();

        assertEquals(numAfterSet, numBeforeSet - 1);

    }

    @Test
    void getDiceWithException() throws IllegalDiceException {
        int k = 3;
        assertThrows(IllegalDiceException.class, () -> d.getDice(k));
    }

    @Test
    void getDiceNoException() throws IllegalDiceException,NotEnoughDiceException {
        d.mix(n);
        int which_Die = new Random().nextInt(expectedDice);
        assertNotNull(d.getDice(which_Die));
        assertEquals(d.getDiceList().get(which_Die), d.getDice(which_Die));
    }

    @Test
    void toStringEmptyTest() {
        assertNotNull(d.toString());
        assertEquals("Dadiera vuota!", d.toString());
    }

    @Test
    void toStringNnEmptyTest() throws NotEnoughDiceException {
        d.mix(n);
        assertNotNull(d.toString());
        assertNotEquals("Dadiera vuota!", d.toString());
    }
}