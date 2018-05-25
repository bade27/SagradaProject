package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Dadiera;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class GridGUI extends GridPane{
        public String num ="5";
    public GridGUI (GridPane p, DadieraGUI dadiera){

        String s="Yellow";
        GridPane grid=new GridPane();
        for(int i=0;i<4;i++){
            for(int j=0;j<5;j++){
                Button b = new Button("  ");
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                grid.add(b,j,i);
                b.setOnAction(new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent event) {
                        if(dadiera.getValDadoTolto()!=null&&dadiera.getColDadoTolto()!=null) {
                            b.setText(dadiera.getValDadoTolto());
                            b.setStyle(dadiera.getColDadoTolto());
                            dadiera.setValDadoTolto();
                            dadiera.setColDadoTolto();
                        }
                    }
                });

            }
        }
        p.add(grid,0,2);
        dimWindows.dim(grid);
    }
     }
