package it.polimi.ingsw;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ObiettiviGUI {
    public ObiettiviGUI(GridPane p){
        GridPane ob=new GridPane();
        Button b =new Button("obiettivo privato");
        b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        ob.add(b,0,0);
        for(int i=0;i<3;i++){
                b =new Button("obiettivo pubblico n° "+(i+1));
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                ob.add(b,0,i+1);
        }
        p.add(ob,2,0);
        dimWindows.dimWidth(ob,300);
    }
}
