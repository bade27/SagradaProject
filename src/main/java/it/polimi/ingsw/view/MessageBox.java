package it.polimi.ingsw.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class MessageBox extends Label {

    public MessageBox(String text) {
        super(text);
    }

    public void updateGraphic(String msg) {
        Platform.runLater(() -> {
            switch (msg) {
                case "Move ok":
                    this.setStyle("-fx-background-color: Green");
                    break;
                default:
                    this.setStyle("-fx-background-color: Red");
                    break;
            }
            this.setAlignment(Pos.CENTER);
            this.setText(msg);
        });
    }
}
