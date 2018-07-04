package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class PlayersGUI extends GridPane {

    private UI game;
    private GridPane players;
    private ArrayList<String> name;



    public PlayersGUI(GridPane root, UI game)
    {
        this.game = game;
        name = new ArrayList<String>();
        players = new GridPane();
        players.setHgap(20);

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

    /**
     * Update opponents player graphic
     * @param pair matrix of pair about opponents' grid
     * @param n opponents' name
     * @param active opponents' status
     */
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

            for (int i = 0; i < players.getChildren().size() ; i++)
                if (((GridPanePlayer)players.getChildren().get(i)).getUser().equals(n))
                    players.getChildren().get(i).setOpacity(0);

            GridPanePlayer onePlayer = new GridPanePlayer(n);
            players.add(onePlayer, index, 0);

            Label l;

            if (active)
                l = new Label(n);
            else
                l = new Label(n+" (uscito)");

            l.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));


            onePlayer.add(l,0,0);

            GridPane grid = new GridPane();
            for (int i = 0; i < pair.length; i++) {
                for (int j = 0; j < pair[i].length; j++) {
                    ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(pair[i][j]));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);

                    imageView.setStyle("-fx-border-color: black;"
                                    + "-fx-border-width: 3;");

                    grid.setMargin(imageView, new Insets(2, 2, 2, 2));

                    //grid.setBorder(new Border());
                    grid.add(imageView, j, i);
                }
            }
            grid.setStyle("-fx-background-color: white; -fx-grid-lines-visible: true");
            grid.setAlignment(Pos.CENTER);
            onePlayer.add(grid, 0, 1);

        });
    }

    //<editor-fold desc="Player class">
    private class GridPanePlayer extends GridPane
    {
        private String user;

        private GridPanePlayer (String u)
        {
            super();
            user = u;
        }


        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
    //</editor-fold>

}

