package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.request.SendMessage;

import java.util.Collections;
import java.util.List;

public interface Callback {
    default List<SendMessage> callback(long chatId, String text) {
        return Collections.singletonList(
                createSendMessage(chatId, text)
        );
    }

    SendMessage createSendMessage(Long chatId, String text);

}
