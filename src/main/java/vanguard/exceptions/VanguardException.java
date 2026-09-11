package vanguard.exceptions;


public class VanguardException extends Exception {
    public VanguardException(String message) {
        super(message);
    }

    public VanguardException(String message, Throwable cause) {
        super(message, cause);
    }
}
