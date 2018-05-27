package it.polimi.ingsw;

import it.polimi.ingsw.remoteInterface.Pair;

public interface GUI {
    public void updateDadiera (Pair[] dadiera);
    public void updateWindow (Pair[][] window);
    public void setEnableBoard(boolean enableBoard);
}
