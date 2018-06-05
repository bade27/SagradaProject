package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class Coordinates  implements Serializable {
    private int i;
    private int j;

    public Coordinates(int x, int y) {
        this.i = x;
        this.j = y;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "X coord: "+ i + " Y coord: " + j;
    }
}
