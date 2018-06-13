package it.polimi.ingsw.model.objectives;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectivesFactoryTest {
    @Test
    void getPrivateObjective() {
        String path = FileLocator.getPrivateObjectivesListPath();
        try {
            ArrayList<String> names = ParserXML.readObjectiveNames(path);
            for(String s : names) {
                assertTrue(ObjectivesFactory.getPrivateObjective(s).getClass() == PrivateObjective.class);
            }
        } catch (ModelException e) {
            e.printStackTrace();
        } catch (ParserXMLException pxmle) {
            pxmle.printStackTrace();
        }
    }

    @Test
    void getPublicObjective() {
        String path = FileLocator.getPublicObjectivesListPath();
        try {
            ArrayList<String> names = ParserXML.readObjectiveNames(path);
            for(String s : names) {
                assertTrue(ObjectivesFactory.getPublicObjective(s).getClass() == PublicObjective.class);
            }
        } catch (ModelException e) {
            e.printStackTrace();
        } catch (ParserXMLException pxmle) {
            pxmle.printStackTrace();
        }
    }

}