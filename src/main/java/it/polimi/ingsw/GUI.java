package it.polimi.ingsw;

import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;

import java.util.ArrayList;

public interface GUI {
    public void updateDadiera (Pair[] dadiera);
    public void updateWindow (Pair[][] window);
    public void setEnableBoard(boolean enableBoard);
    public void updateMessage(String msg);
    public void passTurn();
    public void makeMove();

    public void toolPermission(int i);
    public void setToolPhase(boolean toolPhase);
    public void makeToolMove();

    public void updateTools(String[] toolNames);
    public void updateOpponents(Pair[][] pair, String user);
    public void updateTokens(int n);
    public void updateRoundTrace(ArrayList<Pair>[] trace);
}
