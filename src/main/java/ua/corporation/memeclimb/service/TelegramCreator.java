package ua.corporation.memeclimb.service;

import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.main.MemeMessage;
import ua.corporation.memeclimb.lang.Internationalization;

import java.util.List;

public interface TelegramCreator {
    List<SendMessage> createSendMessages(MemeMessage memeMessage, Internationalization internationalization);

    boolean isSpinAction(MemeMessage memeMessage);
}
