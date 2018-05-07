package Objectives.Private;

import Objectives.ObjectivesFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.spi.ObjectFactory;

import static org.junit.jupiter.api.Assertions.*;

class PrivateObjectiveTest {

    static PrivateObjective obj;

    @BeforeAll
    static void setup() {
        obj = ObjectivesFactory.getPrivateObjective(
                "resources/carte/obbiettivi/obbiettiviPrivati/xml/sfumature_blue.xml");
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