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
    private int nGioc;
    private static String[] toolNames;
    private static ArrayList<String []> cards;
    private static MatchHandler matchHandler;

    @BeforeAll
    static void setUpTools() throws ParserXMLException {
        ArrayList<Integer> validIndices = new ArrayList<>();
        validIndices.add(1);
        validIndices.add(2);
        validIndices.add(3);
        validIndices.add(11);
        ArrayList<String> toolNamesTmp;
        toolNamesTmp = ParserXML.readToolsNames(FileLocator.getToolsListPath());
        for(int i = 0; i < toolNamesTmp.size(); i++)
            if(!validIndices.contains(i))
                toolNamesTmp.remove(i);
        toolNames = toolNamesTmp.toArray(new String[toolNamesTmp.size()]);
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

}