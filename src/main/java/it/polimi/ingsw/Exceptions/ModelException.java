package it.polimi.ingsw.Exceptions;

public class ModelException extends Exception
{
    public ModelException()
    {
        super("it.polimi.ingsw.Model Exception");
    }

    public ModelException(String msg)
    {
        super(msg);
    }
}