package it.polimi.ingsw.view;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * describes the status of the move made
 */
public class MessageBox extends Text {

    public MessageBox(String text) {
        super(text);
        this.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
    }

    /**
     * updates the message displayed on the screen
     * @param msg
     */
    public void updateGraphic(String msg) {
        Platform.runLater(() -> {
            switch (msg) {
                case "Move ok":
                    this.setFill(Color.FORESTGREEN);
                    break;
                case "My turn":
                    this.setFill(Color.STEELBLUE);
                    break;
                case "Turn passed":
                    this.setFill(Color.ORANGERED);
                    break;
                case "Using a tool":
                    this.setFill(Color.BROWN);
                    break;
                default:
                    this.setFill(Color.INDIANRED);
                    break;
            }
            this.setText(msg);
        });
    }
}
