package it.polimi.ingsw;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.*;

public class TurniGUI {
    public TurniGUI(GridPane p) {
        GridPane ob = new GridPane();

        for (int i = 0; i < 10; i++) {
            Button b = new Button("" + i);
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            ob.add(b, i, 0);
        }
        p.add(ob, 0, 0);
        dimWindows.dimHeight(ob,20);
    }
}
