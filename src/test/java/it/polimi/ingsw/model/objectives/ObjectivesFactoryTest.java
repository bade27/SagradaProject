package it.polimi.ingsw.model.objectives;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import static org.junit.jupiter.api.Assertions.*;

class ObjectivesFactoryTest {
    @Test
    void getPrivateObjective() {
        try {
            assertTrue(ObjectivesFactory.getPrivateObjective(
                             "resources/carte/obbiettivi/obbiettiviPrivati/xml/sfumature_blue.xml")
             instanceof PrivateObjective);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getPublicObjective() {
        try {
            assertTrue(ObjectivesFactory.getPublicObjective(
                    "resources/carte/obbiettivi/obbiettiviPubblici/xml/colore/colori_diversi_riga.xml")
                    instanceof PublicObjective);
        } catch (ModelException e) {
            e.printStackTrace();
        }
    }

}