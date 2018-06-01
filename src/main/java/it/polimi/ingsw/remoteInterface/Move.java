package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class Move implements Serializable {

    private Pair p;
    private Integer i;
    private Integer j;

    public Pair getP() {
        return p;
    }
    public void setP(Pair p) {
        this.p = p;
    }

    public Integer getI() {
        return i;
    }
    public Integer getJ() {
        return j;
    }
    public void setIJ(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
