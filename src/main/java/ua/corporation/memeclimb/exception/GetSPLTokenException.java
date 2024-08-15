package ua.corporation.memeclimb.exception;

public class GetSPLTokenException extends RuntimeException{

    public GetSPLTokenException(String message) {
        super(message);
    }

    public GetSPLTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetSPLTokenException(Throwable cause) {
        super(cause);
    }

}
