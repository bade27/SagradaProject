package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;


public class PlayersGUI extends GridPane {

/*  private GridPane root;
    private  GUI game;
    private GridPane singolo_giocatore;
    private GridPane giocatori;

    String [] name;

    public PlayersGUI(GridPane root ,String[]name, GUI game) {
        name = new String[]{"giocatore 1", "giocatore 2", "giocatore 3"};
        giocatori = new GridPane();
        giocatori.setHgap(20);
        this.root = root;
        this.game = game;
        for (int i = 0; i < name.length; i++) {
            singolo_giocatore = new GridPane();
            Label label = new Label(name[i]);                                           //creo una label col nome
            singolo_giocatore.add(label, 0, 0);                      //la piazzo in cima al giocatore
            Pair[][] pair = new Pair[5][4];                                             //matrice di pair
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 5; k++) {
                    pair[k][j] = new Pair(0, ColorEnum.WHITE);
                }
            }
            giocatori.add(singolo_giocatore,i,0);
            updateGraphic(pair, name[i]);

        }
        giocatori.setAlignment(Pos.CENTER);                                             //setto allineamenti vari
        root.add(giocatori, 0, 2);                                   //aggiungo i giocatori alla root

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


    public void updateGraphic(Pair[][] pair,String n) {

        //creazione griglia dato Pair[][]
        GridPane griglia = new GridPane();                                          //creo una griglia
        griglia.setDisable(true);                                                   //disabilito il bottone
        for (int j = 0; j < 4; j++) {                                               //creo un GridPane con dentro i Pair
            for (int i = 0; i < 5; i++) {
                Button cell=new Button();
                griglia.add(cell, i, j);
                cell.setText("" + pair[i][j].getValue());
                cell.setStyle("-fx-background-color: " + pair[i][j].getColor());
                griglia.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }
        griglia.setAlignment(Pos.CENTER);

        //sostituzione della nuova griglia alla vecchia dato n (n=nome del giocatore
        for(int k=0;k<giocatori.getChildren().size();k++) {
            Label l = (Label) singolo_giocatore.getChildren().get(0);
            if(l.getText()==n){
                singolo_giocatore.add(griglia,0,1);
                singolo_giocatore.setAlignment(Pos.CENTER);
//                giocatori.(g, k, 0);
            }
            //GridPane g = (GridPane) giocatori.getChildren().get(k);
            //Label l = (Label) g.getChildren().get(0);
            //if (l.getText() == n) {
            //    g.add(griglia, 0, 1);
            //    g.setAlignment(Pos.CENTER);
            //    giocatori.add(g, k, 0);
            }
        }


    //creazione dello schema nome-->griglia di un singolo giocatore
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