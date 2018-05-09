package it.polimi.ingsw.exceptions;

public class ClientOutOfReachException extends Exception
{
    public ClientOutOfReachException()
    {
        super("it.polimi.ingsw.client out of reach Exception");
    }

    public ClientOutOfReachException(String msg)
    {
        super(msg);
    }
}
