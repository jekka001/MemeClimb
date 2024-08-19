package ua.corporation.memeclimb.exception;

public class NotExpectedException extends RuntimeException {

    public NotExpectedException(String message) {
        super(message);
    }

    public NotExpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpectedException(Throwable cause) {
        super(cause);
    }

}
