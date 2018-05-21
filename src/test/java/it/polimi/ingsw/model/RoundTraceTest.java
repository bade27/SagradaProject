package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundTraceTest {

    private int n;
    private RoundTrace roundtrace;

    @BeforeEach
    void setUp() {
        n=new Random().nextInt(10)+1;
        roundtrace= new RoundTrace();
    }

    @Test()
    void addDice(){
        //numero iniziale di dadi
        int oldLen=roundtrace.getListDice(n).size();

        //creazione dado casuale
        Random col=new Random();
        Random num=new Random();
        int number=num.nextInt(6)+1;
        Color color=null;
        switch (col.nextInt(5))
        {
            case 0:
                color = Color.red;
                break;
            case 1:
                color = Color.green;
                break;
            case 2:
                color = Color.blue;
                break;
            case 3:
                color = Color.yellow;
                break;
            case 4:
                color = Color.magenta;
                break;

            default:
                break;
        }
        Dice dice=new Dice(number,color);

        // funzione numero iniziale di dadi uguali a quello da aggiungere
        BiFunction<ArrayList<Dice>, Dice, Integer> counter = (list, d) -> {
            Long t = list.stream()
                    .filter(currentDice -> currentDice.isEqual(d))
                    .count();
            return t.intValue();
        };

        //numero iniziale di dadi uguali a quello da aggiungere
        int oldnumdice = counter.apply(roundtrace.getListDice(n), dice);

        //aggiunta dado nella track in posizione n-1
        roundtrace.addDice(n, dice);

        //numero attuale di dadi
        int newLen=roundtrace.getListDice(n).size();

        //numero attuale di dadi uguali a quello da aggiungere
        int newnumdice = counter.apply(roundtrace.getListDice(n), dice);

        assertEquals(oldnumdice,newnumdice-1);
        assertEquals(oldLen,newLen-1);
    }

    @Test()
    void deleteDice(){

        //inserisco nella cella n un numero do dadi da 1 a 15 casuale
        int numdice=new Random().nextInt(15)+1;         //numdice=1,2,...,15
        for(int i=0;i<numdice;i++){
            //creazione dado casuale
            Random col=new Random();
            Random num=new Random();
            int number=num.nextInt(6)+1;
            Color color=null;
            switch (col.nextInt(5))
            {
                case 0:
                    color = Color.red;
                    break;
                case 1:
                    color = Color.green;
                    break;
                case 2:
                    color = Color.blue;
                    break;
                case 3:
                    color = Color.yellow;
                    break;
                case 4:
                    color = Color.magenta;
                    break;

                default:
                    break;
            }
            Dice dice=new Dice(number,color);
            roundtrace.addDice(n,dice);
        }

        // funzione numero iniziale di dadi uguali a quello da aggiungere
        BiFunction<ArrayList<Dice>, Dice, Integer> counter = (list, d) -> {
            Long t = list.stream()
                    .filter(currentDice -> currentDice.isEqual(d))
                    .count();
            return t.intValue();
        };

        //estrazione dado casuale dalla posizione n del tracciato
        int c=new Random().nextInt(numdice);
        Dice dice_del=roundtrace.getListDice(n).get(c);

        //numero iniziale di dadi
        int oldLen=roundtrace.getListDice(n).size();

        //numero iniziale di dadi uguali a quello da togliere
        int oldnumdice = counter.apply(roundtrace.getListDice(n), dice_del);

       roundtrace.deleteDice(n,dice_del);

        //numero attuale di dadi
        int newLen=roundtrace.getListDice(n).size();

        //numero attuale di dadi uguali a quello da togliere
        int newnumdice = counter.apply(roundtrace.getListDice(n), dice_del);

        assertEquals(oldnumdice,newnumdice+1);
        assertEquals(oldLen,newLen+1);
    }
}