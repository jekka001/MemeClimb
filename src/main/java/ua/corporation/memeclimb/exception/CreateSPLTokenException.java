package ua.corporation.memeclimb.exception;

public class CreateSPLTokenException extends RuntimeException{

    public CreateSPLTokenException(String message) {
        super(message);
    }

    public CreateSPLTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateSPLTokenException(Throwable cause) {
        super(cause);
    }

}
