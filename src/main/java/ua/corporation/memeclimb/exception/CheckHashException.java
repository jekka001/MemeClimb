package ua.corporation.memeclimb.exception;

public class CheckHashException extends RuntimeException{

    public CheckHashException(String message) {
        super(message);
    }

    public CheckHashException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckHashException(Throwable cause) {
        super(cause);
    }

}
