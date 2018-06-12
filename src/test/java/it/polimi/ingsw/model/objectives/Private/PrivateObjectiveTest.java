package it.polimi.ingsw.model.objectives.Private;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrivateObjectiveTest {

    PrivateObjective obj;
    Window window;

    @BeforeEach
    void setup() throws ModelException, ParserXMLException {

        String path = FileLocator.getPrivateObjectivesListPath();
        ArrayList<String> objs = ParserXML.readObjectiveNames(path);

        obj = ObjectivesFactory.getPrivateObjective(objs.get(new Random().nextInt(objs.size())));

        path = FileLocator.getWindowListPath();
        ArrayList<String[]> wins = ParserXML.readWindowsName(path);

        window = new Window(wins.get(new Random().nextInt(wins.size()))[new Random().nextInt(2)]);
    }



    @Test
    void getName() {
        assertNotNull(obj.getName());
    }

    @Test
    void getDescription() {
        assertNotNull(obj.getDescription());
    }

    @Test
    void getScore() {
        ColorEnum color = obj.getColor();
        int numDice = new Random().nextInt(20) + 1;
        Dice[] dice = IntStream.range(0, numDice)
                .mapToObj(i -> new Dice(new Random().nextInt(6) + 1, color))
                .toArray(Dice[]::new);
        int c = 0;
        for(int i = 0; i < window.getGrid().length; i++)
            for(int j = 0; j < window.getGrid()[0].length; j++)
                if(c < numDice) {
                    try {
                        window.addDice(i, j, dice[c], 0);
                    } catch (IllegalDiceException e) {
                        continue;
                    }
                    c++;
                }

        int total = IntStream.range(0, c).mapToObj(i -> dice[i]).mapToInt(d -> d.getValue()).sum();
        assertEquals(total, obj.getScore(window));
    }
}