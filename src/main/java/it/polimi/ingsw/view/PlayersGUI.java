package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;


public class PlayersGUI extends GridPane {

    private GUI game;
    private GridPane giocatori;

    String[] name;

    public PlayersGUI(GridPane root /*,String[]name*/, GUI game) {


        this.game = game;
        name = new String[]{"A", "B"};
        giocatori = new GridPane();
        giocatori.setOpacity(255);
        giocatori.setHgap(20);

        Pair[][] pair = new Pair[4][5];                                             //matrice di pair
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                pair[i][j] = new Pair(0, ColorEnum.WHITE);
            }
        }

        for (int k = 0; k < name.length; k++) {
            updateGraphic(pair, name[k]);
        }
        giocatori.setAlignment(Pos.CENTER);                                             //setto allineamenti vari
        root.add(giocatori, 0, 3);                                   //aggiungo i giocatori alla root

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

            GridPane singolo_giocatore = new GridPane();
            singolo_giocatore.setOpacity(255);
            //creazione e aggiunta Label con nome
            singolo_giocatore.add(new Label(n), 0, 0);
            //creazione e aggiunta griglia dato Pair[][]
            GridPane griglia = new GridPane();
            griglia.setOpacity(255);
            griglia.setDisable(true);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    Button cell = new Button();
                    cell.setOpacity(255);
                    griglia.add(cell,j,i);
                    cell.setText("" + pair[i][j].getValue());
                    cell.setStyle("-fx-background-color: " + pair[i][j].getColor());
                    griglia.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
            }
            griglia.setAlignment(Pos.CENTER);
            singolo_giocatore.add(griglia, 0, 1);

            //capisco in che posizione è il giocatore e se esiste sostituisco
            int pos = -1;
            for (int k = 0; k < name.length; k++) {
                if (name[k].equals(n)) {
                    pos = k;
                    break;
                }
            }
            if (pos >= 0)
                giocatori.add(singolo_giocatore, pos, 0);
        });
    }
}
