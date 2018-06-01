package it.polimi.ingsw.view;

import javafx.scene.control.Button;

public class EndButton extends Button {

    private Game game;

    public EndButton(String text, Game game) {
        super(text);
        this.game = game;
        this.setOnAction(actionEvent -> {
            game.makeMove();
        });
    }


}
