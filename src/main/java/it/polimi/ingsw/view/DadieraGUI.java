package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class DadieraGUI extends GridPane {

    private GridPane pane;
    private String colore_dado_tolto;
    private String valore_dado_tolto;
    private GridPane grid;
    private Game game;
    private boolean enable;

    public DadieraGUI(GridPane pane, int num,Game game) {
        this.game=game;
        this.pane = pane;
        enable = false;
        grid = new GridPane();
        //dimWindows.dim(grid);
        initGraphic();
        pane.add(grid, 0, 0);
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
                        //b.setStyle("-fx-border-color: " + ColorEnum.YELLOW);
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
                        game.modPair(new Pair(val, color));
                        //game.modPair(new Pair(3,ColorEnum.RED));
                    }
                });
                grid.add(b, i, 0);
            }
        });
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /*public void setta_dadiera() {
        pair = new Pair[numOfDice];

        ColorEnum Randomcolor=ColorEnum.WHITE;
        for (int i = 0; i < pair.length; i++) {
            int colint = new Random().nextInt(5);
            switch (colint) {
                case 0:
                    Randomcolor = ColorEnum.BLUE;
                    break;
                case 1:
                    Randomcolor = ColorEnum.GREEN;
                    break;
                case 2:
                    Randomcolor = ColorEnum.PURPLE;
                    break;
                case 3:
                    Randomcolor = ColorEnum.RED;
                    break;
                case 4:
                    Randomcolor = ColorEnum.YELLOW;
                    break;
                default:
                    break;
            }
            pair[i] = new Pair(new Random().nextInt(6) + 1, Randomcolor);
            Button but=new Button();
            but.setText(""+pair[i].getValue());
            but.setStyle("-fx-background-color: "+pair[i].getColor());
            grid.add(but,i,0);


        }
    }*/
    public String getColDadoTolto() {
        return colore_dado_tolto;
    }

    public String getValDadoTolto() {
        return valore_dado_tolto;
    }

    public void setColDadoTolto() {
        colore_dado_tolto = null;
    }

    public void setValDadoTolto() {
        valore_dado_tolto = null;
    }
}


/*for (int i = 0; i < numOfDice; i++) {
            Button b = new Button("  ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            grid.add(b, i, 0);
            if(pair[i].getValue() != 0)
                b.setText(""+pair[i].getValue());                         //setta numero correttamente
            b.setStyle("-fx-background-color: " + pair[i].getColor());
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(enable&&colore_dado_tolto==null&&valore_dado_tolto==null) {
                        colore_dado_tolto = b.getStyle();
                        valore_dado_tolto = b.getText();
                        b.setText(null);
                        b.setStyle(null);
                    }
                }
            });

        }*/


            /*//bottone di prova
            Button bprova=new Button("P");
            grid.add(bprova,numOfDice,0);
            bprova.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    setta_dadiera();
                }
            });*/