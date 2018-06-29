package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.view.gui.GraphButton;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class ToolsGUI {

    private UI game;
    private GridPane pane;
    private GridPane t;
    private GraphButton b1;
    private GraphButton b2;
    private GraphButton b3;

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
                //First Tool
                b1 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[0]);
                b1.setBackground(new Background(new BackgroundImage(ParserXML.LoadImageXMLAtRequest.getToolsBackground(),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
                b1.setGraphic(new ImageView(new Image(b1.getImgPath())));
                b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b1.setPrefSize(300,200);
                b1.setOnMouseEntered(actionEvent -> {
                    b1.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 5;");
                });

                b1.setOnMouseExited(actionEvent -> {
                    b1.setStyle("");
                });
                b1.setOnAction(actionEvent -> {
                    game.toolPermission(b1.getIdTool());
                });
                t.add(b1, 0, 0);



                //Second Tool
                b2 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[1]);
                b2.setBackground(new Background(new BackgroundImage(ParserXML.LoadImageXMLAtRequest.getToolsBackground(),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
                b2.setGraphic(new ImageView(new Image(b2.getImgPath())));
                b2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b2.setPrefSize(300,200);
                b2.setOnMouseEntered(actionEvent -> {
                    b2.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 5;");
                });

                b2.setOnMouseExited(actionEvent -> {
                    b2.setStyle("");
                });
                b2.setOnAction(actionEvent -> {
                    game.toolPermission(b2.getIdTool());
                });
                t.add(b2, 0, 1);



                //Third Tool
                b3 = GraphicFactory.getToolButtonFromName(FileLocator.getToolsListPath(),name[2]);
                b3.setGraphic(new ImageView(new Image(b3.getImgPath())));
                b3.setBackground(new Background(new BackgroundImage(ParserXML.LoadImageXMLAtRequest.getToolsBackground(),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT)));
                b3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                b3.setPrefSize(300,200);
                b3.setOnMouseEntered(actionEvent -> {
                    b3.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 5;");
                });

                b3.setOnMouseExited(actionEvent -> {
                    b3.setStyle("");
                });
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
