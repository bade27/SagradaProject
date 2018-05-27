package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.model.ColorEnum;

import java.io.Serializable;

public class Pair implements Serializable {

    private int value;
    private ColorEnum color;

    public Pair(int value, ColorEnum color) {
        this.value = value;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public ColorEnum getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Value: " + value + " Color: " + color ;
    }
}
