package it.polimi.ingsw.model.objectives.Private;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrivateObjectiveTest {

    static PrivateObjective obj;

    @BeforeAll
    static void setup() {
        try {
            obj = ObjectivesFactory.getPrivateObjective(
                    "resources/carte/obbiettivi/obbiettiviPrivati/xml/sfumature_blue.xml");
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getName() {
        assertNotNull(obj.getName());
    }

    @Test
    void getDescription() {
        assertNotNull(obj.getDescription());
    }

}