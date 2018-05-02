package Exceptions;

public class ClientOutOfReachException extends Exception
{
    public ClientOutOfReachException()
    {
        super("Client out of reach Exception");
    }

    public ClientOutOfReachException(String msg)
    {
        super(msg);
    }
}
