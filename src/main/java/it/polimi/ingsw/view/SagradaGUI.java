package it.polimi.ingsw.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class
SagradaGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root=new GridPane();
        GridPane pcenter=new GridPane();
        GridPane pleft=new GridPane();
        GridPane pright=new GridPane();
        Label l=new Label();
        root.setGridLinesVisible(true);

        root.add(pleft,0,0);
        root.add(pcenter,1,0);
        root.add(pright,2,0);

        new ToolsGUI(root);
        new RoundsGUI(pcenter);
        //new ConcorrentiGUI(pcenter);
        pcenter.add(l,0,1);
        new GridGUI(pcenter);
        new TargetGUI(root);
        dimWindows.dim(root);
        dimWindows.dim(pright);
        dimWindows.dim(pleft);
        dimWindows.dim(pcenter);

        root.setAlignment(Pos.CENTER);
        pcenter.setAlignment(Pos.CENTER);

        Scene scene=new Scene(root,510,510);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

}
