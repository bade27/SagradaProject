package it.polimi.ingsw.exceptions;

public class IllegalStepException extends  Exception{

    public IllegalStepException(){
        super("Invalid Input");
    }

    public IllegalStepException(String msg)
    {
        super(msg);
    }
}
