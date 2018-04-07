package Test;
import java.awt.*;

public class Dice
{
    private int value;
    private Color color;

    public Dice ()
    {
        value = 0;
        color = Color.black;
    }
    public Dice (int v)
    {
        value = v;
        color = Color.black;
    }
    public Dice (Color c)
    {
        value = 0;
        color = c;
    }
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

    public boolean isEqual (Dice d)
    {
        if (d.getColor() == color && d.getValue() == value)
            return true;
        return false;
    }

    public boolean isSimilar (Dice d)
    {
        if (d.getColor() == color || d.getValue() == value)
            return true;
        return false;
    }
}


