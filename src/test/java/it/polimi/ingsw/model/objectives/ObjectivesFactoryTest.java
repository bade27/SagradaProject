package it.polimi.ingsw.model.objectives;

import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import static org.junit.jupiter.api.Assertions.*;

class ObjectivesFactoryTest {
    @Test
    void getPrivateObjective() {
        assertTrue(ObjectivesFactory.getPrivateObjective(
                         "resources/carte/obbiettivi/obbiettiviPrivati/xml/sfumature_blue.xml")
         instanceof PrivateObjective);
    }

    @Test
    void getPublicObjective() {
        assertTrue(ObjectivesFactory.getPublicObjective(
                "resources/carte/obbiettivi/obbiettiviPubblici/xml/colore/colori_diversi_riga.xml")
                instanceof PublicObjective);
    }

}