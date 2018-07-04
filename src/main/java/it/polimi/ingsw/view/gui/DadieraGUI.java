package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class DadieraGUI extends GridPane {

    private GridPane pane;
    private GridPane grid;
    private UI game;
    private boolean enable;
    private boolean tool;

    public DadieraGUI(GridPane pane, int num, UI game) {
        this.game=game;
        this.pane = pane;
        enable = false;
        grid = new GridPane();
        initGraphic();
        pane.add(grid, 0, 1);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setPadding(new Insets(0,0,10,0));
    }


    /**
     * Graphic initialization with empty dadiera
     */
    private void initGraphic ()
    {
        Pair [] pair = new Pair[9];
        for (int i = 0; i < pair.length; i++)
            pair[i] = new Pair(0, null);
        updateGraphic(pair);
    }

    /**
     * Update dadiera with passed matrix pair
     * @param p matrix pair
     */
    public void updateGraphic(Pair[] p)
    {
        Platform.runLater(() -> {
            grid.getChildren().clear();
            for (int i = 0; i < p.length; i++) {
                DieButton button = new DieButton(p[i].getColor(),p[i].getValue());
                ImageView imageView = new ImageView(GraphicDieHandler.getImageDie(button.getPair()));
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                button.setGraphic(imageView);
                button.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                                "-fx-background-color: transparent;" +
                                "-fx-background-radius: 5;");

                button.setOnMouseEntered(actionEvent -> {
                    button.setStyle("-fx-effect: dropshadow(three-pass-box, white, 20, 0, 0, 0);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 5;");
                });

                button.setOnMouseExited(actionEvent -> {
                    button.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 5;");
                });


                button.setOnAction(event -> {
                    if(enable)
                    {
                        if (tool) {
                            ToolAction.setDadieraPair(button.getPair());
                        } else {
                            MoveAction.setPair(button.getPair());
                        }
                    }
                });
                grid.add(button, i, 0);
            }
        });
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setTool(boolean tool) {
        this.tool = tool;
    }

    /**
     * Class support for graphic handling of dice
     */
    private class DieButton extends Button
    {
        private ColorEnum color;
        private int value;

        private DieButton (ColorEnum c,int v)
        {
            color = c;
            value = v;
        }

        private Pair getPair()
        {
            return new Pair(value,color);
        }
    }

}