package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;
import java.util.ArrayList;

public class PlayersGUI extends GridPane {

    private UI game;
    private GridPane players;
    private ArrayList<String> name;
    GridPane onePlayer=new GridPane();
    public PlayersGUI(GridPane root, UI game) {


        this.game = game;
        name = new ArrayList<String>();
        players = new GridPane();
        //players.setOpacity(255);
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


    public void updateGraphic(Pair[][] pair, String n,boolean active) {
        Platform.runLater(() -> {


            boolean exist=false;
            int index;
            for(index=0;index<name.size();index++){
                if((name.get(index)).equals(n))
                    break;
            }
            if (exist==false){
                name.add(n);
            }
            try{
                players.getChildren().get(index).setOpacity(0);
            }catch (IndexOutOfBoundsException e){
            }

            onePlayer = new GridPane();
            players.add(onePlayer, index, 0);
            //onePlayer.setOpacity(255);
            //creazione e aggiunta Label con nome

            //onePlayer.getChildren().clear();

            if (active)
                onePlayer.add(new Label(n), 0, 0);
            else
                onePlayer.add(new Label(n+" (non più in partita)"), 0, 0);

            //creazione e aggiunta griglia dato Pair[][]
            GridPane grid = new GridPane();
            //grid.setDisable(true);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(pair[i][j]));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);

                    grid.setMargin(imageView, new Insets(2, 2, 2, 2));

                    grid.add(imageView,j,i);
                }
            }
            grid.setAlignment(Pos.CENTER);
            onePlayer.add(grid, 0, 1);
        });
    }

}

