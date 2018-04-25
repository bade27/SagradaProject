package Test.Model;

import java.awt.*;

public class Placement
{
    private int value;
    private Color color;

    public Placement (int v)
    {
        value = v;
        color = null;
    }
    public Placement (Color c)
    {
        value = 0;
        color = c;
    }

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
     * Controlla se il dado passato è esattamente uguale a this
     * @param d dado da confrontare
     * @return true se ugale, false se diverso
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
     * Controlla se il dado passato ha uno dei due parametri uguale a this
     * @param d dado da confrontare
     * @return true se simile, false se diverso
     */
    public boolean isSimilar (Dice d)
    {
        if (d == null)
            return false;
        if (d.getColor() == color || d.getValue() == value || (d.getColor() == null && d.getValue() == 0))
            return true;
        return false;
    }


}
