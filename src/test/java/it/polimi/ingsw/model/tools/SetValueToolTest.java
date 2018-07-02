package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.server.ServerModelAdapter;
import it.polimi.ingsw.server.TokenTurn;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.Wrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SetValueToolTest {

    private ServerModelAdapter adapter;
    private Tools tool;
    private static String[] toolNames;

    @BeforeAll
    static void setUpTools() throws ParserXMLException {
        ArrayList<String> toolNamesTmp;
        toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
        toolNames = new String[] {toolNamesTmp.get(0), toolNamesTmp.get(5)
                , toolNamesTmp.get(6), toolNamesTmp.get(9), toolNamesTmp.get(10)};
    }

    @BeforeEach
    void setup() {
        adapter = new ServerModelAdapter(new Dadiera(), new RoundTrace(), new TokenTurn());
    }


    @Test   //test for the 1st tool
    void useTool1Test() throws ParserXMLException, IllegalStepException
            , IllegalDiceException, NotEnoughDiceException {

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

    @Test   //test for 6th tool
    public void useTool6Test() throws ParserXMLException, ModelException
            , NotEnoughDiceException, IllegalDiceException, IllegalStepException {
        LogFile logFile = new LogFile();
        logFile.createLogFile("tooltest");
        adapter.setLog(logFile);

        tool = ToolsFactory.getTools(toolNames[1].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);

        ArrayList<String[]> cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
        adapter.initializeWindow(cards.get(4)[0]);

        adapter.getDadiera().mix(0);
        Dice d = new Dice(3, ColorEnum.PURPLE);
        adapter.getDadiera().deleteDice(adapter.getDadiera().getDice(0));
        adapter.getDadiera().addDice(d);

        adapter.setCanMove(true);

        adapter.toolRequest(6);
        new Wrapper<>(adapter).myFunction();

        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());
        Tools.setAllToNull();

        new Wrapper<>(d).myFunction();
        tool.use();
        assertEquals(2, tool.getPrice());

        Optional<Dice> maybeD = adapter.getDadiera().getDiceList().stream()
                .filter(die -> die.getColor().equals(ColorEnum.PURPLE)).findFirst();
        Dice toPut = maybeD.get();

        //add the wrong die
        assertThrows(ModelException.class, () -> adapter.addDiceToBoard(1, 4, new Dice(4, ColorEnum.GREEN)));

        //add the correct die
        adapter.addDiceToBoard(1, 4, toPut);
    }

    @Test   //test for 7th tool
    public void useTool7Test() throws ParserXMLException, ModelException
            , NotEnoughDiceException, IllegalDiceException, IllegalStepException {
        LogFile logFile = new LogFile();
        logFile.createLogFile("tooltest");
        adapter.setLog(logFile);

        tool = ToolsFactory.getTools(toolNames[2].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);

        ArrayList<String[]> cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
        adapter.initializeWindow(cards.get(4)[0]);

        adapter.getDadiera().mix(3);
        new Wrapper<>(adapter).myFunction();

        //setting up the players inside the token turn
        adapter.setUser("A");
        adapter.getToken().addPlayer("A");
        adapter.getToken().addPlayer("B");
        adapter.getToken().addPlayer("C");

        adapter.getToken().nextTurn(); //1st turn of A

        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        adapter.getToken().nextTurn(); //1st turn of B
        adapter.getToken().nextTurn(); //1st turn of C

        adapter.getToken().nextTurn(); //2nd turn of C
        adapter.getToken().nextTurn(); //2nd turn of B

        adapter.getToken().nextTurn(); //2nd turn of A

        ArrayList<ColorEnum> dadieraDiceColors = adapter.getDadiera().getDiceList().stream()
                .map(d -> d.getColor()).collect(Collectors.toCollection(ArrayList::new));

        int nDice = adapter.getDadiera().getDiceList().size();

        tool.use();
        adapter.getDadiera().getDiceList().forEach(dice -> assertTrue(dadieraDiceColors.contains(dice.getColor())));
        assertEquals(nDice, adapter.getDadiera().getDiceList().size());
        assertEquals(2, tool.price);

    }

    @Test   //test for 10th tool
    public void useToll10Test() throws ParserXMLException, NotEnoughDiceException,
            IllegalStepException, IllegalDiceException {
        tool = ToolsFactory.getTools(toolNames[3].toString());
        adapter.getDadiera().mix(0);

        //test the absence of parameters (the test fails)
        new Wrapper<>(adapter).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());
        Tools.setAllToNull();

        Dice d = adapter.getDadiera().getDice(0);
        int oldValue = d.getValue();

        //now test the correct use and set up of the tool
        new Wrapper<>(adapter).myFunction();
        new Wrapper<>(d).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        d = adapter.getDadiera().getDice(0);
        int newValue = d.getValue();
        assertEquals(7 - oldValue, newValue);
        Tools.setAllToNull();
    }

    @Test   //test for 11th tool
    void useTool11Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {
        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[4].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);

        LogFile logFile = new LogFile();
        logFile.createLogFile("tooltest");
        adapter.setLog(logFile);

        adapter.getDadiera().mix(0);

        //the window used for this test is kaleidoscopic dream
        ArrayList<String[]> cards = cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
        adapter.initializeWindow(cards.get(0)[0]);

        adapter.setCanMove(true);

        adapter.toolRequest(11);
        //end of the setup

        new Wrapper<>(adapter).myFunction();
        //invalid tool setup
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        String msg = adapter.useTool(new Wrapper(adapter.getDadiera().getDice(0)));
        assertEquals(1, tool.getPrice());

        Optional<ColorEnum> colorEnums = Stream.of(ColorEnum.values())
                .filter(c -> c.toString().equals(msg.toUpperCase()))
                .findFirst();
        Dice d = new Dice(5, colorEnums.get());
        adapter.useTool(new Wrapper(d));
        assertEquals(2, tool.getPrice());
        adapter.addDiceToBoard(0, 3, d);
    }
}