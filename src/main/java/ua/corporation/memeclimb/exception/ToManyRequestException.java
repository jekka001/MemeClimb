package ua.corporation.memeclimb.exception;

public class ToManyRequestException extends RuntimeException {

    public ToManyRequestException(String message) {
        super(message);
    }

    public ToManyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToManyRequestException(Throwable cause) {
        super(cause);
    }

}
