package it.polimi.ingsw.exceptions;

public class ModelException extends Exception
{
    public ModelException()
    {
        super("it.polimi.ingsw.model Exception");
    }

    public ModelException(String msg)
    {
        super(msg);
    }
}