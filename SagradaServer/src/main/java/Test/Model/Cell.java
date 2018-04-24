package Test.Model;

public class Cell
{
    private Dice backDice;
    private Dice frontDice;
    private String imgPath;

    public Cell ()
    {
        backDice = new Dice ();
        imgPath = "";
        frontDice = null;
    }

    public Cell (Dice d)
    {
        backDice = d;
        imgPath = "";
        frontDice = null;
    }

    public Cell (Dice d,String path)
    {
        backDice = d;
        imgPath = path;
        frontDice = null;
    }



    /**
     * Setta il front dice col dado passato se esso è compatibile con il back dice
     * @param d dado da settare front dice
     * @return  true se front dice settato, false altrimenti
     */
    public boolean setDice (Dice d)
    {
        if (!d.isSimilar(backDice))
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

    /**
     * Restituisce il front dice se settato, altrimenti il back dice
     * @return Dice in evidenza
     */
    public Dice getCurrentDice ()
    {
        if (frontDice == null)
            return backDice;
        return frontDice;
    }

    public void setImgPath (String path)
    {
        imgPath = path;
    }

    public Cell clone ()
    {
        Cell copy = new Cell(backDice.clone(),frontDice.clone(),imgPath);
        return copy;
    }


    private Cell (Dice backD,Dice frontD,String p)
    {
        frontDice = frontD;
        backDice = backD;
        imgPath = p;
    }

}


