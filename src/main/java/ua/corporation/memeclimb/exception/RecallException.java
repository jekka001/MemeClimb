package ua.corporation.memeclimb.exception;

public class RecallException extends RuntimeException{

    public RecallException(String message) {
        super(message);
    }

    public RecallException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecallException(Throwable cause) {
        super(cause);
    }

}
