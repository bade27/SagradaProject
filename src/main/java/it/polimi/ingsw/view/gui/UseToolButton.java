package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import javafx.scene.control.Button;

public class UseToolButton extends Button {

    private UI game;
    private boolean enable;

    public UseToolButton(UI game) {
        super();
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
