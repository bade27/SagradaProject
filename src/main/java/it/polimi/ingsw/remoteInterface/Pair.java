package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.model.ColorEnum;

import java.io.Serializable;

public class Pair implements Serializable {

    private Integer value;
    private ColorEnum color;

    public Pair(Integer value) {
        this.value = value;
    }

    public Pair(ColorEnum color) {
        this.color = color;
    }
    public Pair(Integer value, ColorEnum color) {
        this.value = value;
        this.color = color;
    }

    public Integer getValue() {
        return value;
    }

    public ColorEnum getColor() {
        return color;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setColor(ColorEnum color) {
        this.color = color;
    }




    @Override
    public String toString() {
        if (value != 0 && color != null)
            return "D:"+ value + "-" + color;
        else
            return "p:"+ value + "-" + color;
    }
}
