package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import javafx.scene.control.Button;

public class EndButton extends Button {

    private UI game;

    public EndButton(UI game) {
        super();
        this.game = game;
        this.setOnAction(actionEvent -> {
            game.passTurn();
        });
    }


}
