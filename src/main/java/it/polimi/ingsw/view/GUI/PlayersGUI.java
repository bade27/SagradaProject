package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;
import java.util.ArrayList;

public class PlayersGUI extends GridPane {

    private GUI game;
    private GridPane players;
    private ArrayList<String> name;

    public PlayersGUI(GridPane root, GUI game) {


        this.game = game;
        name = new ArrayList<String>();
        players = new GridPane();
        players.setOpacity(255);
        players.setHgap(20);

        Pair[][] pair = new Pair[4][5];                                             //matrice di pair
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                pair[i][j] = new Pair(0, ColorEnum.WHITE);
            }
        }

        players.setAlignment(Pos.CENTER);
        root.add(players, 0, 3);
    }


    //        ____________________________________________________
    //        |                |                |                |
    //        |_____NOME1______|_____NOME2______|_____NOME2______|
    //        |                |                |                |
    //        |                |                |                |
    //        |                |                |                |
    //        |    GRIGLIA1    |    GRIGLIA2    |    GRIGLIA2    |
    //        |                |                |                |
    //        |________________|________________|________________|


    public void updateGraphic(Pair[][] pair, String n) {
        Platform.runLater(() -> {
            GridPane onePlayer = new GridPane();

            boolean exist=false;
            int index;
            for(index=0;index<name.size();index++){
                if((name.get(index)).equals(n))
                    break;
            }
            if (exist==false){ name.add(n); }

            players.add(onePlayer, index, 0);
            onePlayer.setOpacity(255);
            //creazione e aggiunta Label con nome
            onePlayer.add(new Label(n), 0, 0);
            //creazione e aggiunta griglia dato Pair[][]
            GridPane grid = new GridPane();
            grid.setOpacity(255);
            grid.setDisable(true);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    Button cell = new Button();
                    cell.setOpacity(255);
                    grid.add(cell,j,i);
                    cell.setText("" + pair[i][j].getValue());
                    cell.setStyle("-fx-background-color: " + pair[i][j].getColor());
                    grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
            }
            grid.setAlignment(Pos.CENTER);
            onePlayer.add(grid, 0, 1);
        });
    }
}

