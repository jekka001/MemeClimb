package ua.corporation.memeclimb.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.lang.Internationalization;

import java.util.List;

public interface TelegramCreator {
    List<SendMessage> createSendMessages(Update update, boolean isMessage, long chatId, Internationalization internationalization);

    boolean isSpinAction(Update update, boolean isMessage);
}
