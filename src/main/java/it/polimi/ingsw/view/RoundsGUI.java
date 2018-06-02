package it.polimi.ingsw.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class RoundsGUI extends GridPane {
    int turn=3;
    public RoundsGUI(GridPane p) {
        GridPane r = new GridPane();
        for (int i = 0; i < 10; i++) {
            Button b = new Button("  ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            r.add(b, i, 0);
            if(i==turn-1)
                b.setStyle("-fx-background-color: Red");
        }
        DimWindows.dimHeight(r,20);
        r.setAlignment(Pos.TOP_CENTER);
        p.add(r,0,0);
    }
}
