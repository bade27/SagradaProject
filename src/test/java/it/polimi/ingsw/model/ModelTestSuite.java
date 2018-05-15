package it.polimi.ingsw.model;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(JUnitPlatform.class)
@Suite.SuiteClasses({CellTest.class,
        DadieraTest.class, DiceBag.class,
        DiceTest.class,
        Placement.class,
        WindowTest.class
})

public class ModelTestSuite {

}
