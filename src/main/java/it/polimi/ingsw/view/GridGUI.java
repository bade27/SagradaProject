package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class GridGUI extends GridPane{

    Game game;
    private boolean enable;
    private GridPane g;

    public GridGUI (GridPane p, Game game){
        this.game=game;
        enable = false;
        GridPane grid=new GridPane();
        Pair [][] pair=new Pair[5][4];
        for(int j=0;j<4;j++){
            for(int i=0;i<5;i++){
                pair[i][j]=new Pair(0, ColorEnum.WHITE);
                CellButton b = new CellButton(i,j);
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                grid.add(b,i,j);
                b.setOnAction(event -> {
                    if(enable) {
                        game.modIJ(b.geti(),b.getj());
                        game.makeMove();
                        //System.out.println("x:" + b.geti() + ", y:" + b.getj());
                    }
                });
                }
        }
        updateGrid(pair);
        p.add(grid,0,2);
        dimWindows.dim(grid);
    }

    public void updateGrid (Pair[][] pair)
    {
        g=new GridPane();
        for(int j=0;j<4;j++){
            for(int i=0;i<5;i++) {
                CellButton b = new CellButton(i,j);
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                g.add(b,i,j);
                b.setText(""+pair[i][j].getValue());
                b.setStyle("-fx-background-color: " + pair[i][j].getColor());
            }
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

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

