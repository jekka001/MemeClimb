package ua.corporation.memeclimb.entity.main;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.Getter;

@Getter
public class MemeMessage {
    private final long chatId;
    private final int messageId;
    private final boolean isMessage;
    private final String data;
    private final User telegramUser;

    public MemeMessage(Update update) {
        this.isMessage = update.message() != null;
        this.chatId = parseChatId(update);
        this.messageId = parseMessageId(update);
        this.data = parseData(update);
        this.telegramUser = parseTelegramId(update);
    }

    public MemeMessage(Message message) {
        this.isMessage = true;
        this.chatId = message.chat().id();
        this.messageId = message.messageId();
        this.data = message.text();
        this.telegramUser = message.from();
    }

    private long parseChatId(Update update) {
        return isMessage ?
                update.message().chat().id() :
                update.callbackQuery().from().id();
    }

    private int parseMessageId(Update update) {
        return isMessage ?
                update.message().messageId() :
                update.callbackQuery().message().messageId();
    }

    private String parseData(Update update) {
        return isMessage ?
                update.message().text() :
                update.callbackQuery().data();
    }

    private User parseTelegramId(Update update) {
        return isMessage ?
                update.message().from() :
                update.callbackQuery().from();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        MemeMessage secondObj = (MemeMessage) obj;
        return this.chatId == secondObj.getChatId() &&
                this.messageId == secondObj.getMessageId() &&
                this.isMessage == secondObj.isMessage;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (int) (chatId ^ (chatId >>> 32));
        hash = 31 * hash + messageId;
        hash = 31 * hash + (isMessage ? 0 : 1);

        return hash;
    }
}
