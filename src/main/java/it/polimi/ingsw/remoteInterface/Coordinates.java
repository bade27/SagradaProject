package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class Coordinates  implements Serializable {
    private Integer i;
    private Integer j;

    public Coordinates(Integer x, Integer y) {
        this.i = x;
        this.j = y;
    }

    public Integer getI() {
        return i;
    }

    public Integer getJ() {
        return j;
    }

    @Override
    public String toString() {
        return "X coord: "+ i + " Y coord: " + j;
    }
}
