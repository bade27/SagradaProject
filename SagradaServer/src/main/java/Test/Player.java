package Test;

import Test.Exceptions.IllegalDiceException;
import Test.Model.Dice;
import Test.Model.Window;

public class Player
{
    private Window finestra;
    private Dadiera dadiera;

    public Player ()
    {
        finestra = null;
        dadiera = null;
    }

    public Player (Window f,Dadiera d)
    {
        finestra = f;
        dadiera = d;

    }

    public Window getWindow ()
    {
        return finestra;
    }

    public Dadiera getDadiera()
    {
        return dadiera;
    }

    public void addDiceToBoard (int x,int y,Dice d) throws IllegalDiceException
    {
        finestra.addDice(x,y,d);
    }

    public void deleteDiceFromDadiera (Dice d) throws IllegalDiceException
    {
        dadiera.deleteDice(d);
    }
}
