package ua.corporation.memeclimb.exception;

public class ThreadException extends RuntimeException{

    public ThreadException(String message) {
        super(message);
    }

    public ThreadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadException(Throwable cause) {
        super(cause);
    }

}
