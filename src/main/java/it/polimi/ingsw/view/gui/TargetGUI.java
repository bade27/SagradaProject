package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class TargetGUI extends GridPane{
    UI game;
    GridPane t;
    Button intPrivate;
    Button bprivate;

    Button intPublic;
    Button bpublic1;
    Button bpublic2;
    Button bpublic3;
    public TargetGUI(GridPane p, UI game){
        this.game=game;

        bprivate=new Button();
        intPrivate=new Button();
        intPublic=new Button();
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


        t.add(intPrivate,0,0);
        t.add(bprivate,0,1);
        t.add(intPublic,0,2);
        t.add(bpublic1,0,3);
        t.add(bpublic2,0,4);
        t.add(bpublic3,0,5);
        p.add(t,2,0);
        DimWindows.dimWidth(t,250);
    }


    public void updatePrivateTarget(String []s)
    {
        Platform.runLater(() -> {
            String[] vecname0 = s[0].split("\\/");
            String name0 = (vecname0[vecname0.length - 1].split("\\."))[0];

            Image intest = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\obbiettivi_privati.png");
            intPrivate.setGraphic(new ImageView(intest));

            Image image = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\diagonali_colorate.png");
            bprivate.setGraphic(new ImageView(image));

            //bprivate.setText(name0);
            bprivate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }


    public void updatePublicTarget(String []s){
        Platform.runLater(() -> {
            Image intest = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\obbiettivi_pubblici.png");
            intPublic.setGraphic(new ImageView(intest));


            String[] vecname1 = s[0].split("\\/");
            String name1 = (vecname1[vecname1.length - 1].split("\\."))[0];

            Image image1 = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\diagonali_colorate.png");
            bpublic1.setGraphic(new ImageView(image1));
            //bpublic1.setBackground(image1);

            //bpublic1.setText(name1);
            bpublic1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);



            String[] vecname2 = s[1].split("\\/");
            String name2 = (vecname2[vecname2.length - 1].split("\\."))[0];

            Image image2 = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\diagonali_colorate.png");
            bpublic2.setGraphic(new ImageView(image2));

            //bpublic2.setText(name2);
            bpublic2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);



            String[] vecname3 = s[02].split("\\/");
            String name3 = (vecname3[vecname3.length - 1].split("\\."))[0];

            Image image3 = new Image("file:resources\\carte\\obbiettivi\\obbiettiviPubblici\\Images\\diagonali_colorate.png");
            bpublic3.setGraphic(new ImageView(image3));

            //bpublic3.setText(name3);
            bpublic3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }



}
