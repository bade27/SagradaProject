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
    public void game();
    public void endGame(String [] name, int [] record);
    public void loading();
    public void disconnection(String s);
    public void fatalDisconnection(String s);
    public void login(String s);
    public void maps(String[] s1,String[]s2);
    public void updateTools(String[] toolNames);
    public void updateOpponents(Pair[][] pair, String user);
    public void updateTokens(int n);
    public void updatePublicTarget(String [] s);
    public void updatePrivateTarget(String [] s);
    public void updateRoundTrace(ArrayList<Pair>[] trace);
}