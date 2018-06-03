package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class RoundsGUI extends GridPane {
    int turn=3;
    boolean enable=true;
    GridPane round;
    GridPane diceInRound;
    GridPane roundTrace;
    public RoundsGUI(GridPane p, GUI game) {

        Pair[] dice=new Pair[3];                            //creo i pair
        for(int i=0;i<3;i++){
            dice[i]=new Pair(4, ColorEnum.BLUE);
        }
        roundTrace=new GridPane();
        round = new GridPane();                             //creo i round
        diceInRound=new GridPane();

        for (int i = 0; i < 10; i++) {
            Button indexround = new Button();
            indexround.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
            indexround.setText(""+(i+1));
            round.add(indexround, i, 0);
            indexround.setOnAction(event -> {
                Button b = new Button(indexround.getText());
                b.setDisable(true);
                b.setOpacity(255);
                diceInRound.add(b, 0, 0);
                for (int j=0;j<dice.length;j++)
                {
                    b = new Button();
                    b.setDisable(true);
                    b.setStyle("-fx-background-color: White");
                    b.setText("0");
                    b.setOpacity(255);
                    diceInRound.add(b, j+1, 0);
                }
            });
        }



        roundTrace.setAlignment(Pos.TOP_CENTER);
        roundTrace.add(round,0,0);
        roundTrace.add(diceInRound,0,1);
        DimWindows.dimHeight(round,20);
        round.setAlignment(Pos.TOP_CENTER);
        p.add(roundTrace,0,0);
    }
    public void updateRoundTrace(Pair[] pair, int i){

    }
}
