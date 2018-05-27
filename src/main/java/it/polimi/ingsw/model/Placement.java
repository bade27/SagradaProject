package it.polimi.ingsw.model;

import java.awt.*;

public class Placement
{
    private int value;
    private Color color;

    public Placement (int v,Color c)
    {
        value = v;
        color = c;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
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

    public ColorEnum getColorEnum ()
    {
        if (color == null)
            return ColorEnum.WHITE;
        if (color == Color.red)
            return ColorEnum.RED;
        if (color == Color.green)
            return ColorEnum.GREEN;
        if (color == Color.yellow)
            return ColorEnum.YELLOW;
        if (color == Color.blue)
            return ColorEnum.BLUE;
        if (color == Color.MAGENTA)
            return ColorEnum.PURPLE;

        return ColorEnum.WHITE;
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
