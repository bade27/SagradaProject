package it.polimi.ingsw.view;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class ToolsGUI {
    public ToolsGUI(GridPane pane){
        GridPane t = new GridPane();
        for (int i = 0; i < 3; i++) {
            Button b = new Button("tool n° " + (i + 1));
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            t.add(b, 0, i);
        }
        dimWindows.dimWidth(t,300);
        pane.add(t,0,0);
    }
}
