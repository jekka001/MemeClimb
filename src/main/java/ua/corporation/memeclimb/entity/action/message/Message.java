package ua.corporation.memeclimb.entity.action.message;

import com.pengrad.telegrambot.request.SendMessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface Message {
    default List<SendMessage> message(long chatId, String text) {
        return Collections.singletonList(createSendMessage(chatId, text));
    }

    default List<SendMessage> message(long chatId, List<String> textList) {
        return textList.stream()
                .map(text -> createSendMessage(chatId, text))
                .collect(Collectors.toList());
    }

    SendMessage createSendMessage(Long chatId, String text);
}
