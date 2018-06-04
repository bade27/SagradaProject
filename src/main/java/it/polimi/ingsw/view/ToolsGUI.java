package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ToolsGUI {

    private GUI game;
    private GridPane pane;
    private GridPane t;

    public ToolsGUI(GridPane pane,GUI game){

        this.game = game;
        this.pane=pane;

        t = new GridPane();
        int []index={1,2,3};
        String [] name={"tool 1","tool 2","tool3"};
        updateTools(name);
        DimWindows.dimWidth(t,300);
        pane.add(t,0,0);

    }

    public void updateTools(String[] name){
        Platform.runLater(() -> {                                   //name e description sono momentanei,
            Button b1 = new Button(name[0]);                       //dal momento che avremo le immagini delle carte
            b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);    //diventeranno superflui
            b1.setOnAction(actionEvent -> {
                game.toolPermission(1);
            });
            t.add(b1, 0, 0);

            Button b2 = new Button(name[1]);
            b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            b2.setOnAction(actionEvent -> {
                game.toolPermission(2);
            });
            t.add(b2, 0, 1);

            Button b3 = new Button(name[2]);
            b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            b3.setOnAction(actionEvent -> {
                game.toolPermission(6);
            });
            t.add(b3, 0, 2);
        });
    }
}
