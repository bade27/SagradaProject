package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


public class PlayersGUI extends GridPane {

    private GridPane root;
    private GridPane singolo_giocatore;
    private GridPane giocatori;
    String [] name;

    public PlayersGUI(GridPane root, Game game) {
        name= new String[]{"pippo", "pluto","camillo"};
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
            updateGraphic(pair, name[k]);
        }
        giocatori.setAlignment(Pos.CENTER);
        root.add(giocatori, 0, 2);
    }

    public void updateGraphic(Pair[][] pair,String n) {

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

        for(int k=0;k<name.length;k++) {
            GridPane g = (GridPane) giocatori.getChildren().get(k);
            Label l = (Label) g.getChildren().get(0);
            if (l.getText() == n) {
                g.add(griglia, 0, 1);
                g.setAlignment(Pos.CENTER);
                giocatori.add(g, k, 0);
            }
        }
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
