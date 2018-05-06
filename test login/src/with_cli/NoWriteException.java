package with_cli;

public class NoWriteException extends RuntimeException {
    public NoWriteException(String s) {
        System.out.println(s);
    }
}
