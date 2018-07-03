package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.utilities.ParserXML;
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

        BackgroundImage myBI= new BackgroundImage(ParserXML.LoadImageXMLAtRequest.getObjectivesBackground(),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        gridPane.setBackground(new Background(myBI));

        gridPane.add(privateGeneralBox,0,0);
        gridPane.add(publicGeneralBox,0,1);


        EndButton pass = new EndButton(game);
        pass.setBackground(new Background(new BackgroundImage(ParserXML.LoadImageXMLAtRequest.getObjectivesBackground(),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT)));
        pass.setGraphic(new ImageView(new Image("file:resources/carte/obbiettivi/pass_turn.png")));

        pass.setOnMouseEntered(actionEvent -> {
            pass.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);" +
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 5;");
        });

        pass.setOnMouseExited(actionEvent -> {
            pass.setStyle("");
        });

        gridPane.add(pass,0,2);


        gridPane.setPadding(new Insets(10,5,20,5));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        p.add(gridPane,2,0);
        //DimWindows.dimWidth(gridPane,250);
    }


    public void updatePrivateTarget(String []s)
    {
        Platform.runLater(() ->
        {
            //System.out.println(s[0]);
            privateGeneralBox.getChildren().clear();
            VBox intestationBox = new VBox();
            VBox privateObjBox = new VBox();

            //Set images
            intestationBox.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getPrivateIntestation()));
            privateObjBox.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getImageFromXMLPath(s[0])));
            intestationBox.setPadding(new Insets(0,5,10,5));
            privateObjBox.setPadding(new Insets(0,5,10,5));

            //Add images
            privateGeneralBox.getChildren().add(intestationBox);
            privateGeneralBox.getChildren().add(privateObjBox);
        });
    }


    public void updatePublicTarget(String []s)
    {
        //System.out.println(s[0] + " " + s[1] + " "+ s[2]);
        Platform.runLater(() ->
        {
            publicGeneralBox.getChildren().clear();
            VBox intestationBox = new VBox();
            VBox publicObjBox1 = new VBox();
            VBox publicObjBox2 = new VBox();
            VBox publicObjBox3 = new VBox();

            //Set images
            intestationBox.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getPublicIntestation()));
            publicObjBox1.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getImageFromXMLPath(s[0])));
            publicObjBox2.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getImageFromXMLPath(s[1])));
            publicObjBox3.getChildren().add(new ImageView(ParserXML.LoadImageXMLAtRequest.getImageFromXMLPath(s[2])));

            intestationBox.setPadding(new Insets(0,5,10,5));
            publicObjBox1.setPadding(new Insets(0,5,10,5));
            publicObjBox2.setPadding(new Insets(0,5,10,5));
            publicObjBox3.setPadding(new Insets(0,5,10,5));

            //Add images
            publicGeneralBox.getChildren().add(intestationBox);
            publicGeneralBox.getChildren().add(publicObjBox1);
            publicGeneralBox.getChildren().add(publicObjBox2);
            publicGeneralBox.getChildren().add(publicObjBox3);
        });
    }



}
