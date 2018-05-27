package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SetValueToolTest {

    private Dadiera d;
    private int n;
    private int expectedDice;
    private SetValueTool svt;

    @BeforeEach
    void setUp() {
        n = new Random().nextInt(9) + 1;
        d=new Dadiera();
        expectedDice = 2*n + 1;
        svt=new SetValueTool();
    }

    @Test()
    void addSub0() throws IllegalDiceException, IllegalStepException {
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice finalDice=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore 6 controllo se nella dadiera ce ne sono altri con valore != 6
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (finalDice.getValue()==6){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() != 6) {
                        found = true;
                        finalDice = d.getDice(i);
                        break;
                    }
                }
            }
        }
            //valore originale
            int oldval = finalDice.getValue();
            //colore originale
            ColorEnum oldcol=finalDice.getColor();
            //prezzo prima aver usato il tool
            int price1=svt.getPrice();

            //chiamo la funzione
            svt.addSub(finalDice, d, 0);

            //nuovo valore
            int newval = finalDice.getValue();
            //nuovo colore
            ColorEnum newcol=finalDice.getColor();
            //prezzo dopo aver usato il tool
            int price2=svt.getPrice();

            assertEquals(oldval + 1, newval);
            assertEquals(price2,2);
            assertEquals(price1,1);
            assertEquals(oldcol,newcol);
    }

    @Test()
    void addSub0Exception() throws IllegalDiceException {
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice dice=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore !=6 controllo se nella dadiera ce ne sono altri con valore == 6
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (dice.getValue()!=6){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() == 6) {
                        found = true;
                        dice = d.getDice(i);
                        break;
                    }
                }
            }
        }
        Dice finalDice = dice;
        int price=svt.getPrice();
        assertThrows(IllegalStepException.class, () -> svt.addSub(finalDice,d,0));
    }


    @Test()
    void addSub1() throws IllegalDiceException, IllegalStepException {
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice finalDice=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore 1 controllo se nella dadiera ce ne sono altri con valore != 1
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (finalDice.getValue()==1){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() != 1) {
                        found = true;
                        finalDice = d.getDice(i);
                        break;
                    }
                }
            }
        }
        //valore originale
        int oldval = finalDice.getValue();
        //colore originale
        ColorEnum oldcol=finalDice.getColor();
        //prezzo prima aver usato il tool
        int price1=svt.getPrice();

        //chiamo la funzione
        svt.addSub(finalDice, d, 1);

        //nuovo valore
        int newval = finalDice.getValue();
        //nuovo colore
        ColorEnum newcol=finalDice.getColor();
        //prezzo dopo aver usato il tool
        int price2=svt.getPrice();

        assertEquals(oldval - 1, newval);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);
    }

    @Test()
    void addSub1Exception() throws IllegalDiceException {
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice dice=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore !=1 controllo se nella dadiera ce ne sono altri con valore == 1
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (dice.getValue()!=1){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() == 1) {
                        found = true;
                        dice = d.getDice(i);
                        break;
                    }
                }
            }
        }
        Dice finalDice = dice;
        assertThrows(IllegalStepException.class, () -> svt.addSub(finalDice,d,1));
    }

    @Test()
    void turnDice() throws IllegalDiceException {
        d.mix(n);
        int i=new Random().nextInt(expectedDice);
        Dice dice=d.getDice(i);

        int price1=svt.getPrice();
        int oldval=dice.getValue();
        ColorEnum oldcol=dice.getColor();

        //chiamo la funzione
        svt.turnDice(dice,d);

        int price2=svt.getPrice();
        int newval=dice.getValue();
        ColorEnum newcol=dice.getColor();

        assertEquals(7-oldval, newval);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);
    }

    @Test()
    void relaunchDice() throws IllegalDiceException {
        d.mix(n);
        int i=new Random().nextInt(expectedDice);
        Dice dice=d.getDice(i);

        int price1=svt.getPrice();
        ColorEnum oldcol=dice.getColor();

        //chiamo la funzione
        svt.relaunchDice(dice,d);

        int price2=svt.getPrice();
        int newval=dice.getValue();
        ColorEnum newcol=dice.getColor();

        assertNotNull(newval);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);

    }

    @Test()
    void setPrice() {
        int price1=svt.getPrice();
        svt.setPrice();
        int price2=svt.getPrice();
        svt.setPrice();
        int price3=svt.getPrice();
        assertEquals(price1,1);
        assertEquals(price2,2);
        assertEquals(price3,2);
    }

    @Test()
    void getPrice() {
        int price=svt.getPrice();
        assertNotNull(price);
    }
}