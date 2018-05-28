package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class GridGUI extends GridPane{

    Game game;
    private boolean enable;
    private GridPane grid;

    public GridGUI (GridPane p, Game game){
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
        p.add(grid,0,1);
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
                    CellButton b = new CellButton(i, j);
                    b.setPrefSize(100, 80);
                    b.setText("" + pair[i][j].getValue());
                    b.setStyle("-fx-background-color: " + pair[i][j].getColor());
                    b.setOnAction(event -> {
                        if (enable) {
                            game.modIJ(b.geti(), b.getj());
                            game.makeMove();
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

    /**
     * element of the grid
     */
    private class CellButton extends Button{

        int i;
        int j;

        public CellButton(int i, int j){
            this.i=i;
            this.j=j;
        }
        public int geti(){return i;}
        public int getj(){return j;}
    }


}

