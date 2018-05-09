package it.polimi.ingsw.exceptions;

public class IllegalDiceException extends Exception
{
    public IllegalDiceException()
    {
        super("Dice Exception");
    }

    public IllegalDiceException(String msg)
    {
        super(msg);
    }
}