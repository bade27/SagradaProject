package Test;

public class IllegalDiceException extends Exception
{
    public IllegalDiceException()
    {
        super("Dice In Wrong Position");
    }

    public IllegalDiceException(String msg)
    {
        super(msg);
    }
}