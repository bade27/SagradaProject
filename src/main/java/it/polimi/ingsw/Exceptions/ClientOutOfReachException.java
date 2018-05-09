package it.polimi.ingsw.Exceptions;

public class ClientOutOfReachException extends Exception
{
    public ClientOutOfReachException()
    {
        super("it.polimi.ingsw.Client out of reach Exception");
    }

    public ClientOutOfReachException(String msg)
    {
        super(msg);
    }
}
