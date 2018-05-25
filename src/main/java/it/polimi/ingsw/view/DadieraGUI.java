package it.polimi.ingsw.view;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class DadieraGUI extends GridPane{

    private String s;
    private String num;
    private String colore_dado_tolto;
    private String valore_dado_tolto;

    public DadieraGUI(GridPane p) throws IllegalDiceException {
        Dadiera d=new Dadiera();
        d.mix(4);
        GridPane grid = new GridPane();
        //Pair[] Dadieradice;
        int numberofDice = 9;
        //Dadieradice=new Pair[numberofDice];
        for(int i=0;i< 9/*Dadieradice.length*/;i++){
            Button b=new Button(" ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            grid.add(b, i, 0);
        }

        for (int i = 0; i <numberofDice; i++) {
            Button b = new Button("  ");
            b.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
           grid.add(b, i, 0);
            b.setText(""+d.getDice(i).getValue());                         //setta numero correttamente
                num=""+d.getDice(i).getValue()/*dice[0]*/;
            /*if(d.getDice(i).getColor()==Color.red)
                    s="Red";
            else if(d.getDice(i).getColor()==Color.green)
                    s="Green";
            else if(d.getDice(i).getColor()==Color.blue)
                    s="Blue";
            else if(d.getDice(i).getColor()==Color.yellow)
                   s="Yellow";
            else if(d.getDice(i).getColor()==Color.magenta)
                    s="Magenta";*/
            b.setStyle("-fx-background-color: " +ColorEnum.RED);
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
