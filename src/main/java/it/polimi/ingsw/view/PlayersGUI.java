package it.polimi.ingsw.view;

import com.sun.javafx.scene.control.skin.LabeledImpl;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class PlayersGUI extends GridPane {

    private GridPane root;
    private GridPane singolo_giocatore;
    private GridPane giocatori;

    public PlayersGUI(GridPane root, Game game) {
        String [] name= new String[]{"pippo", "pluto","camillo"};
        giocatori=new GridPane();
        giocatori.setHgap(20);
        singolo_giocatore=new GridPane();
        this.root = root;

        for(int k=0;k<name.length;k++) {
            singolo_giocatore=new GridPane();
            singolo_giocatore.add(new Label(name[k]),0,0);
            Pair[][] pair = new Pair[5][4];
            for (int j = 0; j < 4; j++) {
                for (int i = 0; i < 5; i++) {
                    pair[i][j] = new Pair(0, ColorEnum.WHITE);
                }
            }
            updateGraphic(pair, k);
        }
        giocatori.setAlignment(Pos.CENTER);
        root.add(giocatori, 0, 2);
    }

    public void updateGraphic(Pair[][] pair,int index) {

        GridPane griglia = new GridPane();            //griglia
        griglia.setDisable(true);
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                //Label l = new Label();
                Button l=new Button();
                griglia.add(l, i, j);
                l.setText("" + pair[i][j].getValue());
                l.setStyle("-fx-background-color: " + pair[i][j].getColor());
                griglia.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }
        griglia.setAlignment(Pos.CENTER);
        singolo_giocatore.add(griglia, 0, 1);
        singolo_giocatore.setAlignment(Pos.CENTER);
        giocatori.add(singolo_giocatore,index,0);
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
