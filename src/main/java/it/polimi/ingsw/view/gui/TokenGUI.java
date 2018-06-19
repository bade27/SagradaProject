package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import javafx.scene.layout.*;
import javafx.scene.text.*;


public class TokenGUI{
    private UI game;
    private GridPane root;
    private Text t;
    private BorderPane bp;

    public TokenGUI(GridPane root, UI game){
        this.game=game;
        this.root=root;
        bp=new BorderPane();
        int n=0;
        t=new Text("Token:\t");
        updateTockens(n);
        t.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        bp.setCenter(t);
        root.add(bp,0,4);
    }

    public void updateTockens(int num){
        t.setText("Token:\t"+num);
    }
}

