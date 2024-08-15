package ua.corporation.memeclimb.exception;

public class ServerHasNotMoneyException extends RuntimeException{

    public ServerHasNotMoneyException(String message) {
        super(message);
    }

    public ServerHasNotMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerHasNotMoneyException(Throwable cause) {
        super(cause);
    }

}
