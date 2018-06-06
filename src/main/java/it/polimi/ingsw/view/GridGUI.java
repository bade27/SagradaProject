package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GridGUI extends GridPane{

    private GUI game;
    private boolean enable;
    private boolean tool;
    private GridPane grid;

    public GridGUI (GridPane p, GUI game){
        this.game=game;
        enable = false;
        grid=new GridPane();
        Pair [][] pair=new Pair[4][5];
        for(int i = 0 ; i < 4; i++)
            for(int j = 0 ; j < 5; j++)
                pair[i][j]=new Pair(0, ColorEnum.WHITE);
        updateGrid(pair);

        //dimWindows.dim(grid);
        grid.setAlignment(Pos.CENTER);
        p.add(grid,0,2);
    }

    /**
     *
     * @param pair grid of pairs value-color that represent the player's grid
     */
    public void updateGrid (Pair[][] pair)
    {
        Platform.runLater(() -> {
            grid.getChildren().clear();
            //grid = new GridPane();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    CellButton b = new CellButton(i, j,pair[i][j].getColor(),pair[i][j].getValue());
                    b.setPrefSize(100, 80);
                    b.setText("" + pair[i][j].getValue());
                    b.setStyle("-fx-background-color: " + pair[i][j].getColor());
                    if(pair[i][j].getColor() != null && pair[i][j].getValue() != 0)
                        b.setFont(Font.font("ComicSans", FontWeight.EXTRA_BOLD,30));
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


}

