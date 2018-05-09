package it.polimi.ingsw.exceptions;

public class ParserXMLException extends Exception
{
    public ParserXMLException()
    {
        super("XML Parser Exception");
    }

    public ParserXMLException(String msg)
    {
        super(msg);
    }
}


