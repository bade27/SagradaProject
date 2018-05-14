package it.polimi.ingsw;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SagradaFXGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root=new GridPane();       //creo divisione concorrenti|gioco|obiettivi
        GridPane pcenter=new GridPane();
        GridPane pleft=new GridPane();
        GridPane pright=new GridPane();
        root.setGridLinesVisible(true);

        root.add(pleft,0,0);
        root.add(pcenter,1,0);
        root.add(pright,2,0);

        new ConcorrentiGUI(root);
        new TurniGUI(pcenter);
        pcenter.add(new Label("zona gioco"),0,1);
        new GrigliaGUI(pcenter);
        new ObiettiviGUI(root);

        dimWindows.dimensiona(root);
        dimWindows.dimensiona(pright);
        dimWindows.dimensiona(pleft);
        dimWindows.dimensiona(pcenter);

        root.setAlignment(Pos.CENTER);
        pcenter.setAlignment(Pos.CENTER);

        Scene scene=new Scene(root,610,510);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

}
