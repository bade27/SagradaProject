package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class Move implements Serializable {

    private Pair p;
    private int i;
    private int j;

    public Pair getP() {
        return p;
    }

    public void setP(Pair p) {
        this.p = p;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void setIJ(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
