package it.polimi.ingsw.model;
public class Dice
{
    //Il colore di default è null e il valore di default è 0
    private Integer value;
    //private Color color;
    private ColorEnum color;

    /*public Dice (int v,Color c)
    {
        value = v;
        color = c;
    }*/

    public Dice (int v,ColorEnum c)
    {
        value = v;
        color = c;
    }

    public Integer getValue ()
    {
        return value;
    }

    public ColorEnum getColor ()
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

    /*public ColorEnum getColorEnum ()
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
    }*/

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


