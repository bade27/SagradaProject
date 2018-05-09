package it.polimi.ingsw.Exceptions;

public class NotEnoughDiceException extends RuntimeException {
    public NotEnoughDiceException(String s) {
        super(s);
    }
}
