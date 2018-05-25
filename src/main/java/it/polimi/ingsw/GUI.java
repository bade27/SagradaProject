package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientModelAdapter;

public interface GUI {
    public void initGraphic (ClientModelAdapter giocatore);
    public void setEnableBoard(boolean enableBoard);
}
