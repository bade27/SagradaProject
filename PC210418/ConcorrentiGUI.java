package it.polimi.ingsw;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;

public class ConcorrentiGUI {
    public ConcorrentiGUI(GridPane p) {
        GridPane ob = new GridPane();

        for (int i = 0; i < 3; i++) {
            Button b = new Button("giocatore n° " + (i + 1));
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            ob.add(b, 0, i);
        }
        p.add(ob, 0, 0);
        dimWindows.dimWidth(ob,300);
    }
}
