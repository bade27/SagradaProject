package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.utilities.FileLocator;
import javafx.application.Platform;
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
        Platform.runLater(() -> {
            try
            {
                GraphButton b1 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[0]);
                b1.setText(name[0] + "\nTool nr." + b1.getIdTool() );
                b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b1.setOnAction(actionEvent -> {
                    game.toolPermission(b1.getIdTool());
                });
                t.add(b1, 0, 0);

                GraphButton b2 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[1]);
                b2.setText(name[1]  + "\nTool nr." + b2.getIdTool() );
                b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b2.setOnAction(actionEvent -> {
                    game.toolPermission(b2.getIdTool());
                });
                t.add(b2, 0, 1);

                GraphButton b3 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[2]);
                b3.setText(name[2]  + "\nTool nr." + b3.getIdTool() );
                b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b3.setOnAction(actionEvent -> {
                    game.toolPermission(b3.getIdTool());
                });
                t.add(b3, 0, 2);
            }catch (Exception e){
                e.getStackTrace();
            }
        });
    }


}
