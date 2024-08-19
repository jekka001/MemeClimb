package ua.corporation.memeclimb.exception;

public class ServerProblemException extends RuntimeException {

    public ServerProblemException(String message) {
        super(message);
    }

    public ServerProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerProblemException(Throwable cause) {
        super(cause);
    }

}
