package ua.corporation.memeclimb.entity.action.message;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.config.Markup;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;

import java.util.List;

public class Start extends Action implements Message {
    private static final String KEY = "greeting";
    public static final String COMMAND = "/start";


    public Start(Internationalization internationalization) {
        super(internationalization, null, null, null);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = internationalization.getLocalizationMessage(KEY);

        return message(chatId, text);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(Markup.START_BUTTONS.get(internationalization));
    }
}
