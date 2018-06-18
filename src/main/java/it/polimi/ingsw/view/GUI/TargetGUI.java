package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class TargetGUI extends GridPane{
    GUI game;
    GridPane t;
    Button bprivate;
    Button bpublic1;
    Button bpublic2;
    Button bpublic3;
    public TargetGUI(GridPane p, GUI game){
        this.game=game;

        bprivate =new Button();
        bpublic1 =new Button();
        bpublic2 =new Button();
        bpublic3 =new Button();
        t=new GridPane();

        String [] sprivate={"Obiettivo privato"};
        String [] spublic={"Obiettivo pubblico 1","Obiettivo pubblico 2","Obiettivo pubblico 3"};
        try {
            updatePrivateTarget(sprivate);
        }catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            updatePublicTarget(spublic);
        }catch (Exception e1) {
            e1.printStackTrace();
        }
        t.add(bprivate,0,0);
        t.add(bpublic1,0,1);
        t.add(bpublic2,0,2);
        t.add(bpublic3,0,3);
        p.add(t,2,0);
        DimWindows.dimWidth(t,300);
    }


    public void updatePrivateTarget(String []s){
        Platform.runLater(() -> {
            String[] vecname0 = s[0].split("\\/");
            String name0 = (vecname0[vecname0.length - 1].split("\\."))[0];
            bprivate.setText(name0);
            bprivate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }

    public void updatePublicTarget(String []s){
        Platform.runLater(() -> {
            String[] vecname1 = s[0].split("\\/");
            String name1 = (vecname1[vecname1.length - 1].split("\\."))[0];
            bpublic1.setText(name1);
            bpublic1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            String[] vecname2 = s[1].split("\\/");
            String name2 = (vecname2[vecname2.length - 1].split("\\."))[0];
            bpublic2.setText(name2);
            bpublic2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            String[] vecname3 = s[02].split("\\/");
            String name3 = (vecname3[vecname3.length - 1].split("\\."))[0];
            bpublic3.setText(name3);
            bpublic3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }

}
