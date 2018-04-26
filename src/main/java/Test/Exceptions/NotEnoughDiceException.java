package Test.Exceptions;

public class NotEnoughDiceException extends RuntimeException {
    public NotEnoughDiceException(String s) {
        super(s);
    }
}
