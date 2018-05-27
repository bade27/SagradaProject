package it.polimi.ingsw.model;

public class Cell
{
    private Placement back;
    private Dice frontDice;

    public Cell (Placement d)
    {
        back = d;
        frontDice = null;
    }

    /**
     * Set passed die to cell's front die if it is similar to placement
     * @param d die to set
     * @return  true die set, false otherwise
     */
    public boolean setDice (Dice d)
    {
        if (!back.isSimilar(d))
            return false;
        frontDice = d;
        return true;
    }

    /**
     * Set passed die to cell's front die if it has color equals to placement's one
     * @param d die to set
     * @return  true die set, false otherwise
     */
    public boolean setDiceByColor (Dice d)
    {
        if (!back.isColorEquals(d))
            return false;
        frontDice = d;
        return true;
    }

    /**
     * Set passed die to cell's front die if it has value equals to placement's one
     * @param d die to set
     * @return  true die set, false otherwise
     */
    public boolean setDiceByValue (Dice d)
    {
        if (!back.isValueEquals(d))
            return false;
        frontDice = d;
        return true;
    }

    public boolean setFrontDice (Dice d)
    {
        frontDice = d;
        return true;
    }

    public Dice getFrontDice ()
    {
        return frontDice;
    }

    public Placement getPlacement ()
    {
        return back;
    }

    public int getDiceValue ()
    {
        if (frontDice == null)
            return back.getValue();
        else
            return frontDice.getValue();
    }

    public ColorEnum getDiceColor ()
    {
        if (frontDice == null)
            return back.getColor();
        else
            return frontDice.getColor();
    }

}


