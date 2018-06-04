package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class MoveDadieraTraceToolTest {

        private Dadiera d;
        private int n;
        private int expectedDice;
        private MoveDadieraTraceTool mdtt;
        private RoundTrace rt;

        @BeforeEach
        void setUp() {
            n = new Random().nextInt(9) + 1;
            d = new Dadiera();
            expectedDice = 2 * n + 1;               //numero dadi nella dadiera
            rt = new RoundTrace();
        }

        @RepeatedTest(1000) //Test()
        void exchangeDice() throws IllegalDiceException {
            mdtt = new MoveDadieraTraceTool(5,"Taglierina circolare");
            d.mix(n);
            Dice dice_tr;

    //        System.out.println("ELENCO DADI IN DADIERA");
    //        System.out.println("NUM \t\t\t   VALORE\t\t\t\t\t\tCOLORE");
    //        for (int i = 0; i < expectedDice; i++) {
    //            System.out.println("("+(i+1)+") \t\t\t\t"+d.getDice(i).getValue()+"\t\t\t\t"+d.getDice(i).getColor());
    //        }
    //        System.out.println("");

            //estraggo dado a caso dalla dadiera
            Dice dice1 = d.getDice(new Random().nextInt(expectedDice));
    //        System.out.println("DADO ESTRATTO DALLA DADIERA");
    //        System.out.println("VALORE: "+dice1.getValue());
    //        System.out.println("COLORE: "+dice1.getColor());
    //        System.out.println("");
    //        System.out.println("");
    //        System.out.println("");

            //creazione dadi casuali e aggiunta nella roundTrace dei dadi
    //        System.out.println("DADI INSERITI NELLA TRACE:");
    //        System.out.println("RIGA\t\t\t  VALORE\t\t\t\t\t\tCOLORE");
            for (int i = 0; i < 15; i++) {
                Random col = new Random();
                Random num = new Random();
                int number = num.nextInt(6) + 1;
                ColorEnum color = null;
                switch (col.nextInt(5)) {
                    case 0:
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
                dice_tr = new Dice(number, color);
                int index=new Random().nextInt(10) + 1;
                rt.addDice(index, dice_tr);


    //            System.out.println("("+index+") \t\t\t\t"+dice_tr.getValue()+"\t\t\t\t"+dice_tr.getColor());

            }
    //        System.out.println("");

            // funzione dadi uguali a quello inserito
            BiFunction<ArrayList<Dice>, Dice, Integer> numDice = (list, d) -> {
                Long t = list.stream()
                        .filter(currentDice -> currentDice.isEqual(d))
                        .count();
                return t.intValue();
            };

            //prendo un dado a caso da una posizione a caso del tracciato che non sia vuota
            int pos_tr = new Random().nextInt(10) + 1;
            ArrayList<Dice> ld=rt.getListDice(pos_tr);


    //        System.out.println("POSIZIONE NEL TRACE DA DOVE ESTRAGGO IL MIO DADO: "+pos_tr);                                                                                    ///////
    //        System.out.println("ELENCO DADI NELLA CELLA");
    //        for(int i=0;i<ld.size();i++){
    //        System.out.println("VALORE: "+ld.get(i).getValue()+" COLORE: "+ld.get(i).getColor());
    //        }
    //        System.out.println("");


            while(ld.size()==0){
                pos_tr=new Random().nextInt(10) + 1;
                ld=rt.getListDice(pos_tr);


    //            System.out.println("POSIZIONE NEL TRACE DA DOVE ESTRAGGO IL MIO DADO:"+pos_tr);
    //            System.out.println("ELENCO DADI NELLA CELLA");
    //            for(int i=0;i<ld.size();i++){
    //                System.out.println("VALORE: "+ld.get(i).getValue()+" COLORE: "+ld.get(i).getValue());
    //            }

            }
            Dice dice2=ld.get(new Random().nextInt(ld.size()));

    //        System.out.println("DADO ESTRATTO DALLA TRACE");
    //        System.out.println("VALORE: "+dice2.getValue());
    //        System.out.println("COLORE: "+dice2.getColor());
    //        System.out.println("");
    //        System.out.println("");

            //ho la lista, estraggo un dado a caso e lo salvo, applico metodo e
            //vedo se i dadi=d1 sono diminuiti in dadiera e aumentati in trace e
            //vedo se i dadi=d2 sono aumentati in dadiera e diminuiti in trace

            //numero dadi in dadiera prima del metodo
            int totdiceindad1=d.getListaDadi().size();
    //        System.out.println("dadi in dadiera: "+totdiceindad1);

            //numero dadi in tracciato prima del metodo
            int totdiceintrac1=ld.size();
    //        System.out.println("dadi in tracciato: "+totdiceintrac1);

            //numero dei dadi uguali a dice1 in dadiera prima
            int olddice1indadiera = numDice.apply(d.getListaDadi(), dice1);
    //        System.out.println("dadi in dadiera uguali al dado estratto da dadiera: "+olddice1indadiera);

            //numero dei dadi uguali a dice2 in dadiera prima
            int olddice2indadiera = numDice.apply(d.getListaDadi(), dice2);
    //        System.out.println("dadi in dadiera uguali al dado estratto da trace: "+olddice2indadiera);

            //numero dei dadi uguali a dice1 in roundtrace prima
            int olddice1inrt = numDice.apply(ld, dice1);
    //        System.out.println("dadi in trace uguali al dado estratto in dadiera: "+olddice1inrt);

            //numero dei dadi uguali a dice2 in roundtrace prima
            int olddice2inrt = numDice.apply(ld, dice2);
    //        System.out.println("dadi in trace uguali al dado estratto in trace: "+olddice2inrt);

    //        System.out.println("");
    //        System.out.println("APPLICAZIONE DEL METODO exchangeDice");
    //        System.out.println("");

            //applico metodo
            mdtt.setD1(dice1);
            mdtt.setD2(dice2);
            mdtt.setDadiera(d);
            mdtt.setPos_rt(pos_tr);
            mdtt.setRt(rt);

            mdtt.exchangeDice();


            //numero dadi in dadiera dopo il metodo
            int totdiceindad2=d.getListaDadi().size();
    //        System.out.println("dadi in dadiera: "+totdiceindad2);

            //numero dadi in tracciato dopo il metodo
            int totdiceintrac2=ld.size();
    //        System.out.println("dadi in tracciato: "+totdiceintrac2);

            //numero dei dadi uguali a dice1 in dadiera dopo
            int newdice1indadiera = numDice.apply(d.getListaDadi(), dice1);
    //        System.out.println("dadi in dadiera uguali al dado estratto: "+newdice1indadiera);

            //numero dei dadi uguali a dice2 in dadiera dopo
            int newdice2indadiera = numDice.apply(d.getListaDadi(), dice2);
    //        System.out.println("dadi in dadiera uguali al dado del trace: "+newdice2indadiera);

            //numero dei dadi uguali a dice1 in roundtrace dopo
            int newdice1inrt = numDice.apply(ld, dice1);
    //        System.out.println("dadi in trace uguali al dado estratto: "+newdice1inrt);

            //numero dei dadi uguali a dice2 in roundtrace dopo
            int newdice2inrt = numDice.apply(ld, dice2);
    //        System.out.println("dadi in dadiera uguali al dado della dadiera: "+newdice2inrt);

    //        System.out.println("");
    //        System.out.println("dadi in dadiera:");
    //        for (int i = 0; i < expectedDice; i++) {
    //            System.out.println("VALORE: "+d.getDice(i).getValue()+" COLORE: "+d.getDice(i).getColor());
    //        }


            assertEquals(totdiceindad1,totdiceindad2);
            assertEquals(totdiceintrac1,totdiceintrac2);
            if(dice1.getValue()!=dice2.getValue()
                    ||dice1.getColor()!=dice2.getColor()) {
                assertEquals(olddice1indadiera, newdice1indadiera + 1);
                assertEquals(olddice2indadiera, newdice2indadiera - 1);
                assertEquals(olddice1inrt, newdice1inrt - 1);
                assertEquals(olddice2inrt, newdice2inrt + 1);
            }
            else{
                assertEquals(olddice1indadiera, newdice1indadiera);
                assertEquals(olddice2indadiera, newdice2indadiera);
                assertEquals(olddice1inrt, newdice1inrt);
                assertEquals(olddice2inrt, newdice2inrt);
            }
        }

    @Test
    void setPrice() {

        int price1=mdtt.getPrice();
        mdtt.setPrice();
        int price2=mdtt.getPrice();
        mdtt.setPrice();
        int price3=mdtt.getPrice();
        assertEquals(price1,1);
        assertEquals(price2,2);
        assertEquals(price3,2);
    }

    @Test
    void getPrice() {
        int price=mdtt.getPrice();
        assertNotNull(price);
    }

}