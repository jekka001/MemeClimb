package ua.corporation.memeclimb.exception;

public class EmptyBalanceException extends RuntimeException {

    public EmptyBalanceException() {
    }

    public EmptyBalanceException(String message) {
        super(message);
    }

    public EmptyBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
