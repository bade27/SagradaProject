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
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;

public class RoundsGUI extends GridPane {

    private ArrayList<Pair> [] alldice;
    private GridPane round;
    private GridPane diceInRound;
    private GridPane roundTrace;

    public RoundsGUI(GridPane p, UI game)
    {
        alldice = new ArrayList[10];

        for(int i=0;i<alldice.length;i++)
            alldice[i]=new ArrayList<Pair>();

        roundTrace=new GridPane();
        diceInRound=new GridPane();
        round = new GridPane();


        for (int i = 0; i < 10; i++)
        {
            RoundButton indexround = new RoundButton(i);
            indexround.setText(""+(i+1)); //Da sostituire con immagine
            round.add(indexround, i, 0);
            indexround.setActive(false);

            indexround.setOnAction(event1 ->
            {
                if (!indexround.isActive())
                {
                    ArrayList<Pair> arr = alldice[indexround.getRound()];
                    if (arr.size() == 0)
                        return;

                    for (int j = 0 ; j < arr.size() ; j++)
                    {
                        DieButton b = new DieButton(arr.get(j).getColor(),arr.get(j).getValue(),indexround.getRound());

                        b.setText(arr.get(j).getValue().toString()); //Da sostituire con immagine
                        b.setStyle("-fx-background-color: " + arr.get(j).getColor());//Da sostituire con immagine

                        b.setOnAction(event2 -> {
                            ToolAction.setTracePair(b.getPair());
                            //System.out.println(b.getRound()+1);
                            ToolAction.setTracePosition(b.getRound()+1);
                        });

                        diceInRound.add(b,j,0);
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

        DimWindows.dimHeight(round,20);
        round.setAlignment(Pos.TOP_CENTER);

        p.add(roundTrace,0,0);

        GridPane.setMargin(roundTrace,new Insets(0,0,10,0));
        GridPane.setMargin(diceInRound,new Insets(0,0,10,0));


    }


    public void updateRoundTrace(ArrayList<Pair> [] allpair){
        Platform.runLater(() -> {

            alldice = allpair;
            diceInRound.getChildren().clear();
        });
    }

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
}


