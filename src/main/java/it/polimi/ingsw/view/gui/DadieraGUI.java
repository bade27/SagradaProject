package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
        grid.setAlignment(Pos.CENTER);
    }



    private void initGraphic ()
    {
        Pair [] pair = new Pair[9];
        for (int i = 0; i < pair.length; i++)
            pair[i] = new Pair(0, ColorEnum.WHITE);
        updateGraphic(pair);
    }

    public void updateGraphic(Pair[] p)
    {
        Platform.runLater(() -> {
            grid.getChildren().clear();
            for (int i = 0; i < p.length; i++) {
                Button b = new Button("  ");
                b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                Pair current = p[i];
                b.setText("" + current.getValue());
                b.setStyle("-fx-background-color: " + current.getColor());
                b.setOnAction(event -> {
                    if(enable) {
                        String tok = b.getStyle().split(" ")[1];
                        int val = Integer.parseInt(b.getText());
                        ColorEnum color = ColorEnum.WHITE;
                        switch (tok.toLowerCase()) {
                            case "red":
                                color = ColorEnum.RED;
                                break;
                            case "green":
                                color = ColorEnum.GREEN;
                                break;
                            case "yellow":
                                color = ColorEnum.YELLOW;
                                break;
                            case "blue":
                                color = ColorEnum.BLUE;
                                break;
                            case "purple":
                                color = ColorEnum.PURPLE;
                                break;
                            default:
                                break;
                        }
                        if (tool) {
                            ToolAction.setDadieraPair(new Pair(val, color));
                        } else {
                            MoveAction.setPair(new Pair(val, color));
                        }
                    }
                });
                grid.add(b, i, 0);
            }
        });
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setTool(boolean tool) {
        this.tool = tool;
    }

}