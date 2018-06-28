package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.exceptions.NotEnoughDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.server.ServerModelAdapter;
import it.polimi.ingsw.server.TokenTurn;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.Wrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetValueToolTest {

    private ServerModelAdapter adapter;
    private Tools tool;
    private int nGioc;
    private static String[] toolNames;

    @BeforeAll
    static void setUpTools() throws ParserXMLException {
        ArrayList<Integer> validIndices = new ArrayList<>();
        validIndices.add(0);
        validIndices.add(5);
        validIndices.add(9);
        validIndices.add(10);
        ArrayList<String> toolNamesTmp;
        toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
        for(int i = 0; i < toolNamesTmp.size(); i++)
            if(!validIndices.contains(i))
                toolNamesTmp.remove(i);
        toolNames = toolNamesTmp.toArray(new String[toolNamesTmp.size()]);
    }

    @BeforeEach
    void setup() {
        adapter = new ServerModelAdapter(new Dadiera(), new RoundTrace(), new TokenTurn());
    }


    @Test   //test for the 1st tool
    void useToolTest() throws ParserXMLException, IllegalStepException, IllegalDiceException, NotEnoughDiceException {

        tool = ToolsFactory.getTools(toolNames[0].toString());

        Dice[] dadieraDice = IntStream.range(1, 7)
                .mapToObj(n -> new Dice(n, ColorEnum.WHITE))
                .toArray(Dice[]::new);
        adapter.getDadiera().mix(0);
        for(int i = 0; i < dadieraDice.length; i++)
            adapter.getDadiera().addDice(dadieraDice[i]);
        //test the absence of parameters (the test fails)
        new Wrapper<>(adapter).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());

        Tools.setAllToNull();

        //tests what happens if I decrement a 1
        new Wrapper<>(adapter).myFunction();
        new Wrapper<>("dec").myFunction();
        new Wrapper<>(new Dice(1, ColorEnum.WHITE)).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //tests what happens if I increment a 6
        new Wrapper<>(adapter).myFunction();
        new Wrapper<>("inc").myFunction();
        new Wrapper<>(new Dice(6, ColorEnum.WHITE)).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();


        //now test the correct use and set up of the tool
        new Wrapper<>(adapter).myFunction();
        new Wrapper<>("inc").myFunction();
        new Wrapper<>(new Dice(3, ColorEnum.WHITE)).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }


/*
    private Dadiera d;
    private int n;
    private int expectedDice;
    private SetValueTool svt;

    @BeforeEach
    void setUp() {
        n = new Random().nextInt(9) + 1;
        d=new Dadiera();
        expectedDice = 2*n + 1;
    }


    @Test()
    void addSub0() throws IllegalDiceException, IllegalStepException {
        svt=new SetValueTool(1,"Pinza Sgrossatrice");
        int conti1=0;       //dadi uguali a quello estratto prima
        int conti2=0;       //dadi con value +1 a quello estratto prima
        int contf1=0;       //dadi uguali a quello estratto dopo
        int contf2=0;       //dadi con value +1 a quello estratto dopo
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice finalDice1=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore 6 controllo se nella dadiera ce ne sono altri con valore != 6
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (finalDice1.getValue()==6){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() != 6) {
                        found = true;
                        finalDice1 = d.getDice(i);
                        break;
                    }
                }
            }
        }
        //numero di dadi uguale a quello estratto prima dello scambio
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice1.getValue()&&
                    d.getDiceList().get(i).getColor()== finalDice1.getColor()){
                conti1++;
            }
        }
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice1.getValue()+1&&
                    d.getDiceList().get(i).getColor()== finalDice1.getColor()){
                conti2++;
            }
        }
            //colore originale
            ColorEnum oldcol=finalDice1.getColor();
            //prezzo prima aver usato il tool
            int price1=svt.getPrice();
            int num=finalDice1.getValue();
            svt.setD1(finalDice1);
            svt.setDadiera(d);
            svt.setInstruction("inc");

            //chiamo la funzione
            svt.addSub();

            Dice finalDice2=new Dice(num+1,oldcol);
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice2.getValue()-1&&
                    d.getDiceList().get(i).getColor()== finalDice2.getColor()){
                contf1++;
            }
        }
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice2.getValue()&&
                    d.getDiceList().get(i).getColor()== finalDice2.getColor()){
                contf2++;
            }
        }
            //nuovo colore
            ColorEnum newcol=finalDice2.getColor();
            //prezzo dopo aver usato il tool
            int price2=svt.getPrice();
            assertEquals(conti1,contf1+1);
            assertEquals(conti2,contf2-1);
            assertEquals(price2,2);
            assertEquals(price1,1);
            assertEquals(oldcol,newcol);
    }

    @Test()
    void addSub0Exception() throws IllegalDiceException {
        svt=new SetValueTool(1,"Pinza Sgrossatrice");
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
        svt.setD1(finalDice);
        svt.setDadiera(d);
        svt.setInstruction("inc");
        assertThrows(IllegalStepException.class, () -> svt.addSub());
    }


    @RepeatedTest(100) //Test()
    void addSub1() throws IllegalDiceException, IllegalStepException {
        svt=new SetValueTool(1,"Pinza Sgrossatrice");
        int conti1=0;       //dadi uguali a quello estratto prima
        int conti2=0;       //dadi con value +1 a quello estratto prima
        int contf1=0;       //dadi uguali a quello estratto dopo
        int contf2=0;       //dadi con value +1 a quello estratto dopo
        boolean found = false;
        d.mix(n);
        //estraggo dado a caso
        Dice finalDice1=d.getDice(new Random().nextInt(expectedDice));

        //se il dado ha valore 6 controllo se nella dadiera ce ne sono altri con valore != 6
        //altrimenti mixo nuovamente i dadi della dadiera pescandone altri dal bag
        if (finalDice1.getValue()==1){
            while (found==false) {
                d.mix(n);
                for (int i = 0; i < expectedDice; i++) {
                    if (d.getDice(i).getValue() != 1) {
                        found = true;
                        finalDice1 = d.getDice(i);
                        break;
                    }
                }
            }
        }
        //numero di dadi uguale a quello estratto prima dello scambio
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice1.getValue()&&
                    d.getDiceList().get(i).getColor()== finalDice1.getColor()){
                conti1++;
            }
        }
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice1.getValue()-1&&
                    d.getDiceList().get(i).getColor()== finalDice1.getColor()){
                conti2++;
            }
        }

        //colore originale
        ColorEnum oldcol=finalDice1.getColor();
        //prezzo prima aver usato il tool
        int price1=svt.getPrice();
        int num=finalDice1.getValue();
        svt.setD1(finalDice1);
        svt.setDadiera(d);
        svt.setInstruction("dec");

        //chiamo la funzione
        svt.addSub();

        Dice finalDice2=new Dice(num-1,oldcol);
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice2.getValue()+1&&
                    d.getDiceList().get(i).getColor()== finalDice2.getColor()){
                contf1++;
            }
        }
        for(int i=0;i<d.getDiceList().size();i++)
        {
            if(d.getDiceList().get(i).getValue()== finalDice2.getValue()&&
                    d.getDiceList().get(i).getColor()== finalDice2.getColor()){
                contf2++;
            }
        }

        //nuovo colore
        ColorEnum newcol=finalDice2.getColor();
        //prezzo dopo aver usato il tool
        int price2=svt.getPrice();
        assertEquals(conti1,contf1+1);
        assertEquals(conti2,contf2-1);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);
    }

    @Test()
    void addSub1Exception() throws IllegalDiceException {
        svt=new SetValueTool(1,"Pinza Sgrossatrice");
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
        svt.setD1(finalDice);
        svt.setDadiera(d);
        svt.setInstruction("dec");
        assertThrows(IllegalStepException.class, () -> svt.addSub());
    }

    @RepeatedTest(100) //Test()
    void turnDice() throws IllegalDiceException, IllegalStepException {
        svt=new SetValueTool(10,"Tampone Diamantato");
        int conti1=0;       //dadi uguali a quello estratto prima
        int conti2=0;       //dadi con value invertito a quello estratto prima
        int contf1=0;       //dadi uguali a quello estratto dopo
        int contf2=0;       //dadi con value invertito a quello estratto dopo

        d.mix(n);
        int i=new Random().nextInt(expectedDice);

        Dice finaldice1=d.getDice(i);

        int price1=svt.getPrice();

        for(int j=0;j<d.getDiceList().size();j++)
        {
            if(d.getDiceList().get(j).getValue()== finaldice1.getValue()&&
                    d.getDiceList().get(j).getColor()== finaldice1.getColor()){
                conti1++;
            }
        }
        for(int j=0;j<d.getDiceList().size();j++)
        {
            if(d.getDiceList().get(j).getValue()== 7-finaldice1.getValue()&&
                    d.getDiceList().get(j).getColor()== finaldice1.getColor()){
                conti2++;
            }
        }
        int oldval=finaldice1.getValue();
        ColorEnum oldcol=finaldice1.getColor();

        svt.setD1(finaldice1);
        svt.setDadiera(d);

        System.out.println(finaldice1);
        System.out.println(oldval);
        System.out.println(oldcol);
        System.out.println(d);
        System.out.println(conti1);
        System.out.println(conti2);

        //chiamo la funzione
        svt.turnDice();



        Dice finaldice2=new Dice(7-oldval,oldcol);
        for(int j=0;j<d.getDiceList().size();j++)
        {
            if(d.getDiceList().get(j).getValue()== 7-finaldice2.getValue()&&
                    d.getDiceList().get(j).getColor()== finaldice2.getColor()){
                contf1++;
            }
        }
        for(int j=0;j<d.getDiceList().size();j++)
        {
            if(d.getDiceList().get(j).getValue()== finaldice2.getValue()&&
                    d.getDiceList().get(j).getColor()== finaldice2.getColor()){
                contf2++;
            }
        }
        int price2=svt.getPrice();
        int newval=finaldice2.getValue();
        ColorEnum newcol=finaldice2.getColor();

        System.out.println(finaldice2);
        System.out.println(newval);
        System.out.println(newcol);
        System.out.println(d);
        System.out.println(contf1);
        System.out.println(contf2);

        assertEquals(conti1,contf1+1);
        assertEquals(conti2,contf2-1);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);
    }

    @RepeatedTest(100) //Test()
    void relaunchDice() throws IllegalDiceException, IllegalStepException {
        svt=new SetValueTool(6,"Pennello per Pasta Calda");
        d.mix(n);
        int num=new Random().nextInt(expectedDice);
        Dice dice=d.getDice(num);

        int price1=svt.getPrice();
        ColorEnum oldcol=dice.getColor();

        System.out.println(dice.getValue());
        System.out.println(d);
        svt.setD1(dice);
        svt.setDadiera(d);
        //chiamo la funzione
        svt.relaunchDice();

        int price2=svt.getPrice();
        int newval=dice.getValue();
        ColorEnum newcol=dice.getColor();

        System.out.println(dice.getValue());
        System.out.println(d);
        assertNotNull(newval);
        assertEquals(price2,2);
        assertEquals(price1,1);
        assertEquals(oldcol,newcol);

    }

    @Test()
    void setPrice() {
        svt=new SetValueTool(6,"Pennello per Pasta Calda");
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
        svt=new SetValueTool(6,"Pennello per Pasta Calda");
        int price=svt.getPrice();
        assertNotNull(price);
    }*/
}