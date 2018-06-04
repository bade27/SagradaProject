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

    private GridPane root;
    private GUI game;
    private GridPane giocatori;

    String[] name;

    public PlayersGUI(GridPane root /*,String[]name*/, GUI game) {
        name = new String[]{"giocatore 1", "giocatore 2", "giocatore 3"};
        giocatori = new GridPane();
        giocatori.setHgap(20);
        this.root = root;
        this.game = game;
        Pair[][] pair = new Pair[5][4];                                             //matrice di pair
        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 5; k++) {
                pair[k][j] = new Pair(0, ColorEnum.WHITE);
            }
        }
        for (int i = 0; i < name.length; i++) {
            updateGraphic(pair, name[i]);
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
            //creazione e aggiunta Label con nome
            singolo_giocatore.add(new Label(n), 0, 0);
            //creazione griglia dato Pair[][]
            GridPane griglia = new GridPane();                                          //creo una griglia
            griglia.setDisable(true);                                                   //disabilito il bottone
            for (int j = 0; j < 4; j++) {                                               //creo un GridPane con dentro i Pair
                for (int i = 0; i < 5; i++) {
                    Button cell = new Button();
                    griglia.add(cell, i, j);
                    cell.setText("" + pair[i][j].getValue());
                    cell.setStyle("-fx-background-color: " + pair[i][j].getColor());
                    griglia.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }
            }
            griglia.setAlignment(Pos.CENTER);
            singolo_giocatore.add(griglia, 0, 1);
            int pos = -1;
            for (int i = 0; i < name.length; i++) {
                if (name[i] == n) {
                    pos = i;
                    break;
                }
            }
            if (pos >= 0)
                giocatori.add(singolo_giocatore, pos, 0);
        });
    }
}
