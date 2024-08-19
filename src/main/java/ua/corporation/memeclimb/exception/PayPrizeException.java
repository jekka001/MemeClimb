package ua.corporation.memeclimb.exception;

public class PayPrizeException extends RuntimeException {

    public PayPrizeException(String message) {
        super(message);
    }

    public PayPrizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayPrizeException(Throwable cause) {
        super(cause);
    }

}
