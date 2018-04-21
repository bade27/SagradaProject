package it.polimi.ingsw;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class GrigliaFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root=new GridPane();
        int cont=1;
        for(int rowIndex=0;rowIndex<4;rowIndex++){
            RowConstraints rc=new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            root.getRowConstraints().add(rc);
        }
        for(int colIndex=0;colIndex<5;colIndex++){
            ColumnConstraints cc=new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            root.getColumnConstraints().add(cc);
        }
        for(int i=0;i<4;i++){
            for(int j=0;j<5;j++){
                Button b =new Button(""+cont);
                b.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                root.add(b,j,i);
                cont++;
            }
        }
        Scene scene=new Scene(root,700,550);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
