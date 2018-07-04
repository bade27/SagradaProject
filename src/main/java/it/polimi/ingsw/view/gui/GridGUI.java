package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GridGUI extends GridPane{

    private UI game;
    private boolean enable;
    private boolean tool;
    private GridPane grid;

    private Pair [][] precPairs;

    public GridGUI (GridPane p, UI game){
        this.game=game;
        enable = false;
        grid=new GridPane();
        Pair [][] pair=new Pair[4][5];
        precPairs = new Pair[4][5];

        //Creating an empty grid
        for(int i = 0 ; i < 4; i++)
            for(int j = 0 ; j < 5; j++)
            {
                pair[i][j] = new Pair(0, null);
                precPairs[i][j] = new Pair(-1,null);
            }
        updateGrid(pair);

        //dimWindows.dim(grid);
        grid.setAlignment(Pos.CENTER);
        p.add(grid,0,2);
    }

    /**
     * Update grid presentation
     * @param pair grid of pairs value-color that represent the player's grid
     */
    public void updateGrid (Pair[][] pair)
    {
        Platform.runLater(() -> {
            //grid.getChildren().clear();
            for (int i = 0; i < pair.length; i++)
            {
                for (int j = 0; j < pair[i].length; j++)
                {
                    if (precPairs[i][j].getColor() != pair[i][j].getColor() || !precPairs[i][j].getValue().equals(pair[i][j].getValue()))
                    {
                        CellButton b = new CellButton(i, j,pair[i][j].getColor(),pair[i][j].getValue());
                        ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(pair[i][j]));
                        imageView.setFitWidth(75);
                        imageView.setFitHeight(75);
                        b.setGraphic(imageView);


                        b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                                "-fx-background-color: transparent;" +
                                "-fx-background-radius: 5;");

                        b.setOnMouseEntered(actionEvent -> {
                            b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(255,219,163,0.8), 10, 0, 0, 0);" +
                                    "-fx-background-color: transparent;" +
                                    "-fx-background-radius: 5;");
                        });

                        b.setOnMouseExited(actionEvent -> {
                            b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                                    "-fx-background-color: transparent;" +
                                    "-fx-background-radius: 5;");
                        });


                        b.setOnAction(event -> {
                            if (enable) {
                                if(tool) {
                                    ToolAction.setPosition(new Coordinates(b.i,b.j));
                                } else {
                                    MoveAction.setCoord(new Coordinates(b.i,b.j));
                                    game.makeMove();
                                }
                            }
                        });
                        grid.add(b, j, i);
                    }
                }
            }
            precPairs = pair;
        });
    }

    /**
     * enables or disables the grid
     * @param enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setTool(boolean tool) {
        this.tool = tool;
    }

    //<editor-fold desc="Cell class">
    /**
     * element of the grid
     */
    private class CellButton extends Button{

        private int i;
        private int j;

        private ColorEnum color;
        private int value;

        private CellButton(int i, int j,ColorEnum col , int val){
            this.i=i;
            this.j=j;
            color = col;
            value = val;
        }
    }
    //</editor-fold>


}

