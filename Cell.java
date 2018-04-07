package Test;

public class Cell
{
    private Dice backDice;
    private Dice frontDice;
    private String imgPath;

    public Cell ()
    {
        backDice = new Dice ();
        imgPath = "";
    }

    public Cell (Dice d)
    {
        backDice = d;
    }

    public Cell (Dice d,String path)
    {
        backDice = d;
        imgPath = path;
    }

    public boolean setDice (Dice d)
    {
        if (d.isSimilar(backDice))
            return false;
        frontDice = d;
        return true;
    }

    public void setImgPath (String path)
    {
        imgPath = path;
    }
}


