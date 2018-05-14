package it.polimi.ingsw;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class GrigliaGUI {
    public GrigliaGUI(GridPane p){
        GridPane griglia=new GridPane();
        int cont=1;

        for(int i=0;i<4;i++){
            for(int j=0;j<5;j++){
                Button b =new Button(""+cont);
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                griglia.add(b,j,i);
                cont++;
            }
        }
        p.add(griglia,0,3);
        dimWindows.dimensiona(griglia);
    }
}
