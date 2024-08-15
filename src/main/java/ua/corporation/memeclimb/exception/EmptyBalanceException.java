package ua.corporation.memeclimb.exception;

import lombok.Getter;

@Getter
public class EmptyBalanceException extends RuntimeException {

    private long chatId;

    public EmptyBalanceException() {
    }

    public EmptyBalanceException(String message, long chatId) {
        super(message);
        this.chatId = chatId;
    }

    public EmptyBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
