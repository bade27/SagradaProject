package it.polimi.ingsw.exceptions;

public class NotEnoughDiceException extends RuntimeException {
    public NotEnoughDiceException(String s) {
        super(s);
    }
}
