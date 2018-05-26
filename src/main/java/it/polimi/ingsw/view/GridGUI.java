package it.polimi.ingsw.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class GridGUI extends GridPane{
    Game game;
    public GridGUI (GridPane p, DadieraGUI dadiera, Game game){
        this.game=game;
        GridPane grid=new GridPane();
        for(int j=0;j<4;j++){
            for(int i=0;i<5;i++){
                CellButton b = new CellButton(i,j);
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                grid.add(b,i,j);
                b.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        //game.modIJ(b.geti(),b.getj());
                        System.out.println("x:"+b.geti()+", y:"+b.getj());
                    }
                });

            }
        }
        p.add(grid,0,2);
        dimWindows.dim(grid);
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
