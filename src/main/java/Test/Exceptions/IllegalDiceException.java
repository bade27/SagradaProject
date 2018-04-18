package Test.Exceptions;

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