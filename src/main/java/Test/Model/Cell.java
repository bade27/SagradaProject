package Test.Model;

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
     * Setta il front dice col dado passato se esso è compatibile con il placement
     * @param d dado da settare front dice
     * @return  true se front dice settato, false altrimenti
     */
    public boolean setDice (Dice d)
    {
        if (!back.isSimilar(d))
            return false;
        frontDice = d;
        return true;
    }

    public void setFrontDice (Dice d)
    {
        frontDice = d;
    }

    public Dice getFrontDice ()
    {
        return frontDice;
    }

    public Placement getPlacement ()
    {
        return back;
    }

}


