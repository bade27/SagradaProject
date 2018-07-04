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
import javafx.scene.layout.GridPane;

import javafx.scene.control.*;
import java.util.ArrayList;

public class PlayersGUI extends GridPane {

    private UI game;
    private GridPane players;
    private ArrayList<String> name;
    //GridPane onePlayer=new GridPane();




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

            if (active)
                onePlayer.add(new Label(n), 0, 0);
            else
                onePlayer.add(new Label(n+" (uscito)"), 0, 0);

            GridPane grid = new GridPane();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(pair[i][j]));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);

                    imageView.setStyle("-fx-background-color: black;" +
                            "-fx-background-radius: 1;");

                    grid.setMargin(imageView, new Insets(2, 2, 2, 2));
                    grid.add(imageView, j, i);
                }
            }
            grid.setAlignment(Pos.CENTER);
            onePlayer.add(grid, 0, 1);

        });
    }

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

}

