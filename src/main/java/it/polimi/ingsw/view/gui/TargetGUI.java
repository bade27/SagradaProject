package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;


public class TargetGUI extends GridPane{
    UI game;
    private GridPane gridPane;

    private VBox privateGeneralBox;
    private VBox publicGeneralBox;



    public TargetGUI(GridPane p, UI game){
        this.game=game;

        privateGeneralBox = new VBox();
        publicGeneralBox = new VBox();

        gridPane =new GridPane();

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

        BackgroundImage myBI= new BackgroundImage(new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/sfondo_obbiettivi.png",250,62,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        gridPane.setBackground(new Background(myBI));

        gridPane.add(privateGeneralBox,0,0);
        gridPane.add(publicGeneralBox,0,1);

        gridPane.setPadding(new Insets(10,5,20,5));
        gridPane.setHgap(10);
        gridPane.setVgap(10);


        p.add(gridPane,2,0);
        DimWindows.dimWidth(gridPane,250);
    }


    public void updatePrivateTarget(String []s)
    {
        Platform.runLater(() -> {


            Image intest = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/obbiettivi_privati.png");
            Image image = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/diagonali_colorate.png");

            VBox intestationBox = new VBox();
            VBox privateObjBox = new VBox();
            privateGeneralBox.getChildren().clear();

            intestationBox.setPadding(new Insets(0,5,10,5));
            privateObjBox.setPadding(new Insets(0,5,10,5));
            intestationBox.getChildren().add(new ImageView(intest));
            privateObjBox.getChildren().add(new ImageView(image));

            privateGeneralBox.getChildren().add(intestationBox);
            privateGeneralBox.getChildren().add(privateObjBox);
        });
    }


    public void updatePublicTarget(String []s){
        Platform.runLater(() -> {

            Image intest = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/obbiettivi_pubblici.png");
            Image image1 = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/diagonali_colorate.png");
            Image image2 = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/diagonali_colorate.png");
            Image image3 = new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/diagonali_colorate.png");

            VBox intestationBox = new VBox();
            VBox publicObjBox1 = new VBox();
            VBox publicObjBox2 = new VBox();
            VBox publicObjBox3 = new VBox();

            publicGeneralBox.getChildren().clear();

            intestationBox.setPadding(new Insets(0,5,10,5));
            publicObjBox1.setPadding(new Insets(0,5,10,5));
            publicObjBox2.setPadding(new Insets(0,5,10,5));
            publicObjBox3.setPadding(new Insets(0,5,10,5));
            intestationBox.getChildren().add(new ImageView(intest));
            publicObjBox1.getChildren().add(new ImageView(image1));
            publicObjBox2.getChildren().add(new ImageView(image2));
            publicObjBox3.getChildren().add(new ImageView(image3));

            publicGeneralBox.getChildren().add(intestationBox);
            publicGeneralBox.getChildren().add(publicObjBox1);
            publicGeneralBox.getChildren().add(publicObjBox2);
            publicGeneralBox.getChildren().add(publicObjBox3);
        });
    }



}
