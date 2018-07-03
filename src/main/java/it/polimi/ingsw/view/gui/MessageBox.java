package it.polimi.ingsw.view.gui;

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
        Platform.runLater(() ->
        {
            if (msg.equals("Richiesta utilizzo tool accolta") || msg.equals("E' il tuo turno")
                    || msg.equals("Uso tool eseguito corretamente") || msg.equals("Mossa applicata correttamente"))
                this.setFill(Color.STEELBLUE);
            else
                this.setFill(Color.INDIANRED);

            this.setText(msg);
        });
    }
}
