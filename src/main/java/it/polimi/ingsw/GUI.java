package it.polimi.ingsw;

import it.polimi.ingsw.remoteInterface.Pair;

public interface GUI {
    public void updateDadiera (Pair[] dadiera);
    public void updateWindow (Pair[][] window);
    public void setEnableBoard(boolean enableBoard);
    public void updateMessage(String msg);
    public void passTurn();
    public void modMoveIJ(int i, int j);
    public void makeMove();
    public void modMovePair(Pair pair);
    public void toolPermission(int i);
    public void setToolPhase(boolean toolPhase);
    public void modToolMovePair(Pair p);
    public void modToolMoveInstruction(String instruction);
    public void modToolMoveIJ(int i, int j);
    public void makeToolMove();
}
