package it.polimi.ingsw;

import it.polimi.ingsw.model.ModelTestSuite;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        Result results = JUnitCore.runClasses(ModelTestSuite.class);
        for(Failure failure :results.getFailures()) {

        }
    }
}
