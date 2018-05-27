package it.polimi.ingsw.model;

import java.awt.*;

public class Placement
{
    private int value;
    private ColorEnum color;

    public Placement (int v,ColorEnum c)
    {
        value = v;
        color = c;
    }

    public int getValue() {
        return value;
    }

    public ColorEnum getColor() {
        return color;
    }

    /**
     * Check if die passed is equals to this placement in color and value
     * @param d die to compare
     * @return true equals, false different
     */
    public boolean isEqual (Dice d)
    {
        if (d == null)
            return false;
        if (d.getColor() == color && d.getValue() == value)
            return true;
        return false;
    }

    /**
     * Check if die passed is similar to this placement in color or value
     * @param d die to compare
     * @return true similar, false different
     */
    public boolean isSimilar (Dice d)
    {
        if (d == null)
            return false;
        if (d.getColor() == color|| d.getValue() == value || (color == null && value == 0))
            return true;
        return false;
    }

    /**
     * Check if die passed is equals to this placement in color
     * @param d die to compare
     * @return true equals, false different
     */
    public boolean isColorEquals (Dice d)
    {
        if (d.getColor() == color || color == null)
            return true;
        return false;
    }

    /**
     * Check if die passed is equals to this placement in value
     * @param d die to compare
     * @return true equals, false different
     */
    public boolean isValueEquals (Dice d)
    {
        if (d.getValue() == value || value == 0)
            return true;
        return false;
    }

    /**
     *
     * @return String representation of placement
     */
    @Override
    public String toString() {
        return "Dice{" +
                "value=" + value +
                ", color=" + color.toString() +
                '}';
    }
}
