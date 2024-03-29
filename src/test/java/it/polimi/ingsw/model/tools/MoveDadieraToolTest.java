package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.server.MatchHandler;
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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveDadieraToolTest {

    private ServerModelAdapter adapter;
    private Tools tool;
    private int nGioc;
    private static String[] toolNames;
    private static ArrayList<String []> cards;
    private static MatchHandler matchHandler;

    @BeforeAll
    static void setUpTools() throws ParserXMLException {
        ArrayList<String> toolNamesTmp;
        toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
        toolNames = new String[]{toolNamesTmp.get(4), toolNamesTmp.get(8)};
        cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
    }

    @BeforeEach
    void setup() {
        adapter = new ServerModelAdapter(new Dadiera(), new RoundTrace(), new TokenTurn());
    }

    @Test   //test for the 5th tool
    void useTool5Test() throws ParserXMLException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {

        tool = ToolsFactory.getTools(toolNames[0].toString());

        //setup dice in dadiera
        Dice[] dadieraDice = IntStream.range(1, 7)
                .mapToObj(n -> new Dice(n, ColorEnum.YELLOW))
                .toArray(Dice[]::new);
        adapter.getDadiera().mix(0);
        for(int i = 0; i < dadieraDice.length; i++)
            adapter.getDadiera().addDice(dadieraDice[i]);
        adapter.getDadiera().deleteDice(adapter.getDadiera().getDice(0));

        //setup round trace
        Dice d = new Dice(5, ColorEnum.GREEN);
        adapter.getRoundTrace().addDice(1, d);

        //dice to be exchanged
        Dice d1 = adapter.getDadiera().getDice(1);
        Pair p = adapter.getRoundTrace().getPair()[0].get(0);
        Dice d2 = new Dice(p.getValue(), p.getColor());


        //test the absence of parameters (the test fails)
        new Wrapper(adapter).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());
        Tools.setAllToNull();

        //test fails due to wrong index
        new Wrapper<>(-1).myFunction();
        assertEquals(1, tool.getPrice());
        Tools.setAllToNull();


        //now test the correct use and set up of the tool
        new Wrapper<>(1).myFunction();
        new Wrapper<>(d1).myFunction();
        new Wrapper<>(d2).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }

   @Test    //test for 9th tool
    void useTool9Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {

        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[1].toString());  //actual tool
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);

        LogFile logFile = new LogFile();
        logFile.createLogFile("tooltest");
        adapter.setLog(logFile);

        //fill dadiera
        Dice d1 = new Dice(1, ColorEnum.RED);
        Dice d2 = new Dice(2, ColorEnum.YELLOW);
        Dice d3 = new Dice(3, ColorEnum.RED);
        adapter.getDadiera().mix(0);
        adapter.getDadiera().deleteDice(adapter.getDadiera().getDice(0));
        adapter.getDadiera().addDice(d1);
        adapter.getDadiera().addDice(d2);
        adapter.getDadiera().addDice(d3);

        //the window used for this test is shadow thief
        adapter.initializeWindow(cards.get(8)[1]);
        //i row, j col
        adapter.setCanMove(true);
        adapter.addDiceToBoard(2, 0, d1);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(3, 0, d2);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(3, 1, d3);

        adapter.setCanMove(true);
        adapter.toolRequest(2);
        //end of the setup

        new Wrapper<>(adapter).myFunction();
        //invalid tool setup
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //proper setup and use of the tool
        new Wrapper<>(new Dice(5, ColorEnum.BLUE)).myFunction();
        new Wrapper<>(new Coordinates(0, 4)).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }

}