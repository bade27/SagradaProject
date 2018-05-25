package it.polimi.ingsw.view;

import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Game extends Application {


    private int num;
    private Pair buff;

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        DadieraGUI d = new DadieraGUI(root, num);
        GridGUI g = new GridGUI(root, d);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
