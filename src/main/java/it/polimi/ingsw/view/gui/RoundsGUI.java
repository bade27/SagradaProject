package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;

public class RoundsGUI extends GridPane {

    private ArrayList<Pair> [] alldice;
    private GridPane round;
    private GridPane diceInRound;
    private GridPane roundTrace;
    private int currentTurn;

    public RoundsGUI(GridPane p, UI game)
    {
        alldice = new ArrayList[10];
        currentTurn = 0;

        for(int i=0;i<alldice.length;i++)
            alldice[i]=new ArrayList<Pair>();

        roundTrace=new GridPane();
        diceInRound=new GridPane();
        round = new GridPane();

        for (int i = 0; i < 10; i++)
        {
            RoundButton indexround = new RoundButton(i);

            ImageView imageView = new ImageView(GraphicDieHandler.getImageRound(i+1));
            imageView.setFitWidth(45);
            imageView.setFitHeight(45);
            indexround.setGraphic(imageView);
            setButtonStyle(indexround,i);

            GridPane.setMargin(indexround,new Insets(0,0,0,0));


            round.add(indexround, indexround.getRound()+1, 0);
            indexround.setActive(false);

            indexround.setOnAction(event1 ->
            {
                if (!indexround.isActive())
                {
                    diceInRound.getChildren().clear();
                    ArrayList<Pair> arr = alldice[indexround.getRound()];
                    if (arr.size() == 0)
                        return;

                    for (int j = 0 ; j < arr.size() ; j++)
                    {
                        DieButton b = new DieButton(arr.get(j).getColor(),arr.get(j).getValue(),indexround.getRound());

                        ImageView imageView1 = new ImageView(GraphicDieHandler.getImageDie(b.getPair()));
                        imageView1.setFitWidth(30);
                        imageView1.setFitHeight(30);
                        b.setGraphic(imageView1);

                        setButtonStyle(b);

                        b.setOnAction(event2 -> {
                            ToolAction.setTracePair(b.getPair());
                            ToolAction.setTracePosition(b.getRound()+1);
                        });

                        diceInRound.add(b,j+1,0);
                    }
                    indexround.setActive(true);
                }
                else
                {
                    diceInRound.getChildren().clear();
                    indexround.setActive(false);
                }

            });

        }

        roundTrace.setAlignment(Pos.TOP_CENTER);
        roundTrace.add(round,0,0);
        roundTrace.add(diceInRound,0,1);
        round.setAlignment(Pos.TOP_CENTER);

        p.add(roundTrace,0,0);

        GridPane.setMargin(roundTrace,new Insets(5,0,10,0));
        GridPane.setMargin(diceInRound,new Insets(0,0,0,0));


    }


    public void updateRoundTrace(ArrayList<Pair> [] allpair){
        Platform.runLater(() -> {

            alldice = allpair;
            diceInRound.getChildren().clear();

            for (int i=0 ; i < allpair.length ; i++)
            {
                if (allpair[i].size() == 0)
                {
                    currentTurn = i;
                    break;
                }
            }
            for (int i = 0 ; i < 10 ; i++)
                setButtonStyle((RoundButton)round.getChildren().get(i),i);

        });
    }

    //<editor-fold desc="Button class">
    private class RoundButton extends Button
    {
        private int round;
        private boolean active;

        private RoundButton (int r)
        {
            super();
            round=r;
        }

        public int getRound() {
            return round;
        }

        public void setRound(int round) {
            this.round = round;
        }


        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    private class DieButton extends Button
    {
        private ColorEnum color;
        private int value;
        private int round;

        private DieButton (ColorEnum c,int v,int r)
        {
            color = c;
            value = v;
            round = r;
        }

        private Pair getPair()
        {
            return new Pair(value,color);
        }

        public int getRound() {
            return round;
        }

        public void setRound(int round) {
            this.round = round;
        }
    }
    //</editor-fold>

    private void setButtonStyle (Button b,int turn)
    {
        if (turn == currentTurn)
            b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,255,255,0.8), 30, 0, 0, 0);" +
                    "-fx-background-color: transparent;");
        else
            b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                "-fx-background-color: transparent;");

        b.setOnMouseEntered(actionEvent -> {
            b.setStyle("-fx-effect: dropshadow(three-pass-box, white, 20, 0, 0, 0);" +
                    "-fx-background-color: transparent;");
        });

        b.setOnMouseExited(actionEvent -> {
            if (turn == currentTurn)
                b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,255,255,0.8), 30, 0, 0, 0);" +
                        "-fx-background-color: transparent;");
            else
                b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                        "-fx-background-color: transparent;");
        });
    }

    private void setButtonStyle (Button b)
    {
        b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                    "-fx-background-color: transparent;");

        b.setOnMouseEntered(actionEvent -> {
            b.setStyle("-fx-effect: dropshadow(three-pass-box, white, 20, 0, 0, 0);" +
                    "-fx-background-color: transparent;");
        });

        b.setOnMouseExited(actionEvent -> {
                b.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                        "-fx-background-color: transparent;");
        });
    }
}


