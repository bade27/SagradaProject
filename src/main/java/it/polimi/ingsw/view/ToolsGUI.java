package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ToolsGUI {

    private GUI game;

    public ToolsGUI(GridPane pane, GUI game){

        this.game = game;

        GridPane t = new GridPane();

        Button b1 = new Button("tool n° 1");
        b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        b1.setOnAction(actionEvent -> {
            game.toolPermission(1);
            game.setToolPhase(true);
        });
        t.add(b1, 0, 0);

        Button b2 = new Button("tool n° 2");
        b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        t.add(b2, 0, 1);

        Button b3 = new Button("tool n° 3");
        b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        t.add(b3, 0, 2);

        DimWindows.dimWidth(t,300);
        pane.add(t,0,0);
    }
}
