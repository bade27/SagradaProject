package it.polimi.ingsw.view;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class PlayersGUI extends GridPane{
    public PlayersGUI(GridPane p) {
            GridPane ob = new GridPane();
            for (int i = 0; i < 3; i++) {
                Button b = new Button("giocatore "+ (i+1));
                b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                ob.add(b, i, 0);
            }
            dimWindows.dimHeight(ob,20);
            p.add(ob,0,1);
        }
}
