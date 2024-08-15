package ua.corporation.memeclimb.exception;

public class SPLTokenExistException extends RuntimeException{

    public SPLTokenExistException(String message) {
        super(message);
    }

    public SPLTokenExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPLTokenExistException(Throwable cause) {
        super(cause);
    }

}
