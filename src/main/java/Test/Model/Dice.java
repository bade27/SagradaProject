package Test.Model;
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


