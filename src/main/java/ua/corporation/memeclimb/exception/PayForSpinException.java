package ua.corporation.memeclimb.exception;

public class PayForSpinException extends RuntimeException{

    public PayForSpinException(String message) {
        super(message);
    }

    public PayForSpinException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayForSpinException(Throwable cause) {
        super(cause);
    }

}
