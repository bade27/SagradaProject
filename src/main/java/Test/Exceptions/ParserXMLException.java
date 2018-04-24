package Test.Exceptions;

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


