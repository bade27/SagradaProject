package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.view.gui.GraphButton;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class ToolsGUI {

    private UI game;
    private GridPane pane;
    private GridPane t;
    GraphButton b1;
    GraphButton b2;
    GraphButton b3;

    public ToolsGUI(GridPane pane,UI game){

        this.game = game;
        this.pane=pane;

        t = new GridPane();
        String [] name={"tool 1","tool 2","tool3"};
        updateTools(name);
        DimWindows.dimWidth(t,300);
        pane.add(t,0,0);

    }

    public void updateTools(String[] name){
        Platform.runLater(() -> {
            try
            {
                b1 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[0]);

                //b1.setText(name[0] + "\nTool nr." + b1.getIdTool() );
                Image image1 = new Image("file:resources/carte/tools/Images/tool_1.png");
                b1.setGraphic(new ImageView(image1));

                b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b1.setPrefSize(300,200);
                b1.setOnAction(actionEvent -> {
                    game.toolPermission(b1.getIdTool());
                });
                t.add(b1, 0, 0);


                b2 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[1]);

                //b2.setText(name[1]  + "\nTool nr." + b2.getIdTool() );
                Image image2 = new Image("file:resources/carte/tools/Images/tool_1.png");
                b2.setGraphic(new ImageView(image2));

                b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b2.setPrefSize(300,200);
                b2.setOnAction(actionEvent -> {
                    game.toolPermission(b2.getIdTool());
                });
                t.add(b2, 0, 1);



                b3 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[2]);
                //b3.setText(name[2]  + "\nTool nr." + b3.getIdTool() );
                Image image3 = new Image("file:resources/carte/tools/Images/tool_1.png");
                b3.setGraphic(new ImageView(image3));

                b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b3.setPrefSize(300,200);
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
