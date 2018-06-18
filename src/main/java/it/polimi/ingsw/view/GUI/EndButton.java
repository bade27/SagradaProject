package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import javafx.scene.control.Button;

public class EndButton extends Button {

    private GUI game;

    public EndButton(GUI game) {
        super("Fine turno");
        this.game = game;
        this.setOnAction(actionEvent -> {
            game.passTurn();
        });
    }


}
