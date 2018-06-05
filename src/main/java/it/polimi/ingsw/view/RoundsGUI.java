package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class RoundsGUI extends GridPane {
    int turn=3;
    boolean enable=true;
    GridPane round;
    GridPane diceInRound;
    GridPane roundTrace;
    public RoundsGUI(GridPane p, GUI game) {

        ArrayList<Pair> pair = new ArrayList<>();                            //creo i pair
        /*for(int i=0;i<pair.length;i++){
            pair[i]=new Pair(0, ColorEnum.WHITE);
        }*/
        roundTrace=new GridPane();
        round = new GridPane();
        diceInRound=new GridPane();

        for (int i = 0; i < 10; i++) {                                  //creo i round
            Button indexround = new Button();                           //creo bottone
            indexround.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);   //set dimensioni
            indexround.setText(""+(i+1));                               //nome bottone
            round.add(indexround, i, 0);                       //aggiungo bottone a rt
            updateRoundTrace(pair,i);
        }
        roundTrace.setAlignment(Pos.TOP_CENTER);
        roundTrace.add(round,0,0);
        roundTrace.add(diceInRound,0,1);
        DimWindows.dimHeight(round,20);
        round.setAlignment(Pos.TOP_CENTER);
        p.add(roundTrace,0,0);
    }
    public void updateRoundTrace(ArrayList<Pair> pair, int i){
        Platform.runLater(() -> {
            Button indexround = (Button) round.getChildren().get(i);
            indexround.setOnAction(event -> {
                Button b = new Button(indexround.getText() + "\t");
                b.setDisable(true);
                b.setOpacity(255);
                diceInRound.add(b, 0, 0);
                for (int j = 0; j < pair.size(); j++) {
                    b = new Button();
                    b.setDisable(true);
                    b.setStyle("-fx-background-color: " + pair.get(j).getColor());
                    b.setText("" + pair.get(j).getValue());
                    b.setOpacity(255);
                    diceInRound.add(b, j + 1, 0);
                }

            });
        });
    }
}
