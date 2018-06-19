package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.GUI;
import javafx.scene.control.Button;

public class UseToolButton extends Button {

    private GUI game;
    private boolean enable;

    public UseToolButton(String text, GUI game) {
        super(text);
        this.setOnAction(actionEvent -> {
            if(enable)
                game.makeToolMove();
        });
        this.setVisible(false);
    }

    public void enable(boolean enable) {
        this.enable = enable;
        this.setVisible(enable);
    }
}
