package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.remoteInterface.Coordinates;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveGridGridToolTest {

    private ServerModelAdapter adapter;
    private Tools tool;
    private static String[] toolNames;
    private static ArrayList<String []> cards;
    private static MatchHandler matchHandler;

    @BeforeAll
    static void setUpTools() throws ParserXMLException {
        ArrayList<String> toolNamesTmp;
        toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
        toolNames = new String[] {toolNamesTmp.get(1), toolNamesTmp.get(2)
                , toolNamesTmp.get(3), toolNamesTmp.get(11)};
        cards = ParserXML.readWindowsName(FileLocator.getWindowListPath());
    }

    @BeforeEach
    void setup() {
        adapter = new ServerModelAdapter(new Dadiera(), new RoundTrace(), new TokenTurn());
        LogFile logFile = new LogFile();
        logFile.createLogFile("tooltest");
        adapter.setLog(logFile);
    }

    @Test   //test for 2nd tool
    void useTool2Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {
        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[0].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);
        adapter.getDadiera().mix(0);
        adapter.getDadiera().addDice(new Dice(1, ColorEnum.YELLOW));
        adapter.getDadiera().addDice(new Dice(2, ColorEnum.GREEN));
        //the window used for this test is kaleidoscopic dream
        //it'll attempt to move the green dice from cell (0, 1) into the blue-expecting cell (1, 0)
        adapter.initializeWindow(cards.get(0)[0]);
        //i row, j col
        Dice d1 = adapter.getDadiera().getDice(1);
        Dice d2 = adapter.getDadiera().getDice(2);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 0, d1);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(1, 0, d2);

        adapter.toolRequest(2);
        //end of the setup

        new Wrapper<>(adapter).myFunction();
        //invalid tool setup
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //proper setup and use of the tool
        new Wrapper<>(new Coordinates(1, 0)).myFunction();
        new Wrapper<>(new Coordinates(0, 1)).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }

    @Test   //test for 3rd tool
    void useTool3Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {
        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[1].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);
        adapter.getDadiera().mix(0);
        adapter.getDadiera().addDice(new Dice(1, ColorEnum.PURPLE));
        adapter.getDadiera().addDice(new Dice(5, ColorEnum.GREEN));
        //the window used for this test is firmitas
        //it'll attempt to move the green dice from cell (0, 1) into the blue-expecting cell (1, 0)
        adapter.initializeWindow(cards.get(0)[1]);
        //i row, j col
        Dice d1 = adapter.getDadiera().getDice(1);
        Dice d2 = adapter.getDadiera().getDice(2);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 0, d1);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(1, 0, d2);

        adapter.toolRequest(3);
        //end of the setup

        new Wrapper<>(adapter).myFunction();
        //invalid tool setup
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //proper setup and use of the tool
        new Wrapper<>(new Coordinates(1, 0)).myFunction();
        new Wrapper<>(new Coordinates(0, 1)).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }

    @Test   //test for 4th tool
    void useTool4Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {
        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[2].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);
        adapter.getDadiera().mix(0);
        adapter.getDadiera().addDice(new Dice(1, ColorEnum.YELLOW));
        adapter.getDadiera().addDice(new Dice(5, ColorEnum.GREEN));
        adapter.getDadiera().addDice(new Dice(3, ColorEnum.BLUE));
        //the window used for this test is kaleidoscopic dream
        //it'll attempt to move the green dice from cell (0, 1) into the blue-expecting cell (1, 0)
        adapter.initializeWindow(cards.get(0)[0]);
        //i row, j col
        Dice d1 = adapter.getDadiera().getDice(1);
        Dice d2 = adapter.getDadiera().getDice(2);
        Dice d3 = adapter.getDadiera().getDice(3);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 0, d1);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(1, 0, d2);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 1, d3);


        adapter.toolRequest(4);
        //end of the setup

        new Wrapper<>(adapter).myFunction();

        //invalid tool setup
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //just one die
        new Wrapper<>(new Coordinates(0, 0)).myFunction();
        new Wrapper<>(new Coordinates(0, 2)).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //invalid die final position
        new Wrapper<>(new Coordinates(0, 0)).myFunction();
        new Wrapper<>(new Coordinates(0, 2)).myFunction();
        new Wrapper<>(new Coordinates(1, 0)).myFunction();
        new Wrapper<>(new Coordinates(3, 1)).myFunction();
        assertThrows(IllegalStepException.class, () -> tool.use());
        assertEquals(1, tool.getPrice());

        Tools.setAllToNull();

        //proper setup and use of the tool
        new Wrapper<>(new Coordinates(0, 0)).myFunction();
        new Wrapper<>(new Coordinates(0, 2)).myFunction();
        new Wrapper<>(new Coordinates(1, 0)).myFunction();
        new Wrapper<>(new Coordinates(1, 1)).myFunction();

        tool.use();
        assertEquals(2, tool.getPrice());
        Tools.setAllToNull();
    }

    @Test   //test for 12th tool
    void useTool12Test() throws ParserXMLException, ModelException, IllegalStepException,
            IllegalDiceException, NotEnoughDiceException {
        //setting up the "environment"
        tool = ToolsFactory.getTools(toolNames[3].toString());
        Tools[] ts = new Tools[]{tool, ToolsFactory.getTools(toolNames[0].toString()),
                ToolsFactory.getTools(toolNames[0].toString())};
        adapter.setToolCards(ts);

        //setting round trace
        Dice dr1 = new Dice(3, ColorEnum.RED);
        adapter.getRoundTrace().addDice(1, dr1);
        Dice dr2 = new Dice(3, ColorEnum.PURPLE);
        adapter.getRoundTrace().addDice(1, dr2);

        //setting dadiera
        adapter.getDadiera().mix(0);
        adapter.getDadiera().deleteDice(adapter.getDadiera().getDice(0));
        adapter.getDadiera().addDice(new Dice(1, ColorEnum.YELLOW));
        adapter.getDadiera().addDice(new Dice(4, ColorEnum.GREEN));
        adapter.getDadiera().addDice(new Dice(3, ColorEnum.RED));
        adapter.getDadiera().addDice(new Dice(5, ColorEnum.YELLOW));
        adapter.getDadiera().addDice(new Dice(6, ColorEnum.RED));
        adapter.getDadiera().addDice(new Dice(4, ColorEnum.GREEN));
        adapter.getDadiera().addDice(new Dice(1, ColorEnum.PURPLE));
        adapter.getDadiera().addDice(new Dice(6, ColorEnum.YELLOW));
        //the window used for this test is fractal drops
        adapter.initializeWindow(cards.get(5)[0]);

        Dice d1 = adapter.getDadiera().getDice(0);
        Dice d2 = adapter.getDadiera().getDice(1);
        Dice d3 = adapter.getDadiera().getDice(2);
        Dice d4 = adapter.getDadiera().getDice(3);
        Dice d5 = adapter.getDadiera().getDice(4);
        Dice d6 = adapter.getDadiera().getDice(5);
        Dice d7 = adapter.getDadiera().getDice(6);
        Dice d8 = adapter.getDadiera().getDice(7);

        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 0, d1);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 1, d2);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(0, 2, d3);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(1, 1, d4);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(2, 1, d5);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(3, 2, d6);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(3, 3, d7);
        adapter.setCanMove(true);
        adapter.addDiceToBoard(3, 4, d8);
        adapter.setCanMove(true);

        adapter.toolRequest(12);
        //end of the setup

        adapter.useTool();
        assertEquals(1, tool.getPrice());


        adapter.toolRequest(12);
        adapter.useTool(new Wrapper(new Coordinates(3, 3))
                , new Wrapper(new Coordinates(2, 4)), new Wrapper(dr2));
        assertEquals(2, tool.getPrice());

        Tools.setAllToNull();

        adapter.setCanMove(true);
        adapter.toolRequest(12);

        adapter.useTool( new Wrapper(dr1)
                , new Wrapper(new Coordinates(0, 2)), new Wrapper(new Coordinates(1, 3))
                , new Wrapper<>(new Coordinates(2, 1)), new Wrapper(new Coordinates(1, 0)));
        assertEquals(2, tool.getPrice());

    }

}