package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class PlayersGUI extends GridPane {

    private GridPane p;

    public PlayersGUI(GridPane p,String [] name, SagradaGUI game) {
        name= new String[]{"pippo", "pluto","camillo"};
        this.p = p;
        for(int k=0;k<name.length;k++) {
            Pair[][] pair = new Pair[5][4];
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 5; i++) {
                    pair[i][j] = new Pair(0, ColorEnum.WHITE);
                }
            }
            p.add(new Label(name[k]), k, 0);
            updateGraphic(pair, k);
        }
    }

    public void updateGraphic(Pair[][] pair,int index) {

        GridPane griglia = new GridPane();            //griglia

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                Label l = new Label();
                griglia.add(l, i, j);
                l.setText("" + pair[i][j].getValue());
                l.setStyle("-fx-background-color: " + pair[i][j].getColor());
                griglia.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }
        p.add(griglia, index, 1);
    }

    /*//greazione dello schema nome-->griglia di un singolo giocatore
    private void Player(String name, Pair[][] pair) {
        GridPane nome_griglia = new GridPane();
        Label Lname = new Label("name");
        GridPane grigliaPlayer = new GridPane();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                Button b = new Button();
                b.setText("" + pair[i][j]);
                b.setStyle("-fx-background-color: " + pair[i][j].getColor());
                grigliaPlayer.add(b, i, j);
            }
            nome_griglia.add(Lname, 0, 0);
            nome_griglia.add(grigliaPlayer, 0, 1);
        }*/
}
