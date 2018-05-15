package it.polimi.ingsw.model;
import java.awt.*;

public class Dice
{
    //Il colore di default è null e il valore di default è 0
    private int value;
    private Color color;

    public Dice (int v,Color c)
    {
        value = v;
        color = c;
    }

    public int getValue ()
    {
        return value;
    }

    public Color getColor ()
    {
        return color;
    }

    public void setValue(int v) { value = v; }

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
        if (d.getColor() == color || d.getValue() == value || (color == null && value == 0))
            return true;
        return false;
    }

    /**
     *
     * @return * una copia del dado*
     */
    public Dice cloneDice()
    {
        Dice copy = new Dice(this.value,this.color);
        return copy;
    }

    /**
     *
     * @return *la rappresentazione in formato String del dado*
     */
    @Override
    public String toString() {
        return "Dice{" +
                "value=" + value +
                ", color=" + color.toString() +
                '}';
    }
}


