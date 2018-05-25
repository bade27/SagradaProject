package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class Move implements Serializable {

    private String state;

    private Pair p;
    private int i;
    private int j;

    public Move() {
        state = "pair";
    }

    public synchronized Pair getP() {
        return p;
    }

    public synchronized void setP(Pair p) {
        this.p = p;
        state = "grid";
    }

    public synchronized int getJ() {
        return j;
    }

    public synchronized void setIJ(int i, int j) {
        if(state.equals("grid")) {
            this.i = i;
            this.j = j;
            state = "pair";
        }
    }
}
