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
    GridPane onePlayer=new GridPane();

    ArrayList<PairPlayer> upponents;



    public PlayersGUI(GridPane root, UI game)
    {
        this.game = game;
        name = new ArrayList<String>();
        players = new GridPane();
        players.setHgap(20);
        upponents = new ArrayList<>();

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
                //players.getChildren().get(index).setOpacity(0);
            }catch (IndexOutOfBoundsException e){
            }

            onePlayer = new GridPane();
            players.add(onePlayer, index, 0);

            if (active)
                onePlayer.add(new Label(n), 0, 0);
            else
                onePlayer.add(new Label(n+" (non più in partita)"), 0, 0);

            exist = false;
            for (int k = 0 ; k < upponents.size() ; k++)
            {
                if (upponents.get(k).getUser().equals(n))
                {

                    GridPane grid = upponents.get(k).getGrid();
                    Pair[][] precPairs = upponents.get(k).getPair();
                    for (int i = 0; i < 4; i++)
                    {
                        for (int j = 0; j < 5; j++)
                        {
                            if (precPairs[i][j].getColor() != pair[i][j].getColor() || !precPairs[i][j].getValue().equals(pair[i][j].getValue()))
                            {
                                ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(pair[i][j]));
                                imageView.setFitWidth(20);
                                imageView.setFitHeight(20);

                                grid.setMargin(imageView, new Insets(2, 2, 2, 2));
                                grid.add(imageView, j, i);
                            }
                        }
                    }
                    grid.setAlignment(Pos.CENTER);
                    onePlayer.add(grid, 0, 1);
                    upponents.get(k).setPair(pair);
                    upponents.get(k).setGrid(grid);

                    exist = true;
                    break;
                }
                else
                {
                    exist = false;
                    break;
                }
            }

            if (!exist)
            {
                System.out.println("created");
                GridPane grid = new GridPane();
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

                upponents.add(new PairPlayer(n,pair,grid));
            }

        });
    }

    private class PairPlayer
    {
        private String user;
        private Pair [][] pair;
        private GridPane grid;

        private PairPlayer (String u,Pair [][] p,GridPane gr)
        {
            user = u;
            pair = p;
            grid = gr;
        }

        public String getUser() {
            return user;
        }

        public Pair[][] getPair() {
            return pair;
        }

        public void setPair (Pair[][] p){
            pair = p;
        }

        public GridPane getGrid() {
            return grid;
        }

        public void setGrid(GridPane grid) {
            this.grid = grid;
        }
    }

}

