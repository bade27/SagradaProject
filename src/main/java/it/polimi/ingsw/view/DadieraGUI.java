package it.polimi.ingsw.view;

import it.polimi.ingsw.remoteInterface.Pair;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class DadieraGUI extends GridPane{

    private int numPlayers;
    private int numOfDice;
    private String colore_dado_tolto;
    private String valore_dado_tolto;
    private Pair[] pair;

    public DadieraGUI(GridPane p, int num) {
        numPlayers = num;
        numOfDice = numPlayers * 2 + 1;
        GridPane grid = new GridPane();
        for(int i=0;i< numOfDice;i++){
            Button b=new Button(" ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            grid.add(b, i, 0);
        }

        for (int i = 0; i < numOfDice; i++) {
            Button b = new Button("  ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            grid.add(b, i, 0);
            b.setText(""+pair[i].getValue());                         //setta numero correttamente
            b.setStyle("-fx-background-color: " + pair[i].getColor());
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(colore_dado_tolto==null&&valore_dado_tolto==null) {
                        colore_dado_tolto = b.getStyle();
                        valore_dado_tolto = b.getText();
                        b.setText(null);
                        b.setStyle(null);
                    }
                }
            });

        }
        p.add(grid, 0, 1);
        dimWindows.dimHeight(grid, 20);
    }
    public String getColDadoTolto(){return colore_dado_tolto; }

    public String getValDadoTolto(){return valore_dado_tolto; }

    public void setColDadoTolto(){colore_dado_tolto=null; }

    public void setValDadoTolto(){valore_dado_tolto=null; }
}
