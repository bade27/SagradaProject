package it.polimi.ingsw.view;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class TargetGUI {
    public TargetGUI(GridPane p){
        GridPane t=new GridPane();
        Button b =new Button("obiettivo privato");
        b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        t.add(b,0,0);
        for(int i=0;i<3;i++){
                b =new Button("obiettivo pubblico n° "+(i+1));
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                t.add(b,0,i+1);
        }
        p.add(t,2,0);
        DimWindows.dimWidth(t,300);
    }
}
