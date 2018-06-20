package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class RoundsGUI extends GridPane {

    ArrayList<Dice> [] trace;
    int turn=3;
    boolean enable=true;
    UI game;
    ArrayList<Pair> [] alldice;
    GridPane round;
    GridPane diceInRound;
    GridPane roundTrace;
    public RoundsGUI(GridPane p, UI game) {
        this.game=game;
        ArrayList<Pair> listpair = new ArrayList<>();
        listpair.add(new Pair(0,ColorEnum.WHITE));
        listpair.add(new Pair(0,ColorEnum.WHITE));
        listpair.add(new Pair(0,ColorEnum.WHITE));
        alldice=new ArrayList[10];
        for(int i=0;i<alldice.length;i++) {
            alldice[i]=listpair;
        }

        roundTrace=new GridPane();

        round = new GridPane();
        for (int i = 0; i < 10; i++) {                                  //creo i round
            Button indexround = new Button();                           //creo bottone
            indexround.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);   //set dimensioni
            indexround.setText(""+(i+1));                               //nome bottone
            round.add(indexround, i, 0);                       //aggiungo bottone a rt
        }

        diceInRound=new GridPane();
        updateRoundTrace(alldice);

        roundTrace.setAlignment(Pos.TOP_CENTER);
        roundTrace.add(round,0,0);
        roundTrace.add(diceInRound,0,1);
        it.polimi.ingsw.view.DimWindows.dimHeight(round,20);
        round.setAlignment(Pos.TOP_CENTER);
        p.add(roundTrace,0,0);
    }
    public void updateRoundTrace(ArrayList<Pair> [] allpair){
        Platform.runLater(() -> {
            diceInRound.getChildren().clear();
            for(int i=0;i<allpair.length;i++) {
                alldice[i] = allpair[i];
                Button indexround=(Button)round.getChildren().get(i);
                int finalI = i;
                indexround.setOnAction(event -> {
                    diceInRound.getChildren().clear();
                    diceInRound=new GridPane();
                    Button b = new Button(indexround.getText() + "\t");
                    b.setDisable(false);
                    b.setOpacity(255);
                    diceInRound.add(b, 0, 0);

                    for (int j = 0; j < allpair[finalI].size(); j++) {
                        b = new Button();
                        //b.setDisable(true);
                        b.setStyle("-fx-background-color: " + allpair[finalI].get(j).getColor());
                        b.setText("" + allpair[finalI].get(j).getValue());
                        b.setOpacity(255);
                        diceInRound.add(b, j + 1, 0);
                        Button finalB = b;
                        int finalJ = j;
                        b.setOnAction(event1 -> {
                            int value=allpair[finalI].get(finalJ).getValue();
                            ColorEnum color =allpair[finalI].get(finalJ).getColor();
                            //game.modToolMovePair(new Pair(value,color));
                            ToolAction.setTracePair(new Pair(value,color));
                            ToolAction.setTracePosition(finalI+1);
                        });
                    }
                    roundTrace.add(diceInRound,0,1);
                });
            }

            /*Button indexround = (Button) round.getChildren().get(i);

                Button b = new Button(indexround.getText() + "\t");
                b.setDisable(true);
                b.setOpacity(255);
                diceInRound.add(b, 0, 0);
                for (int j = 0; j < pair.length; j++) {
                    b = new Button();
                    b.setDisable(true);
                    b.setStyle("-fx-background-color: " + pair[j].getColor());
                    b.setText("" + pair[j].getValue());
                    b.setOpacity(255);
                    diceInRound.add(b, j + 1, 0);
                }
            });*/
        });
    }

}
