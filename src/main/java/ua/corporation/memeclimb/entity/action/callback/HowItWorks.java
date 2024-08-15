package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;

import java.util.List;

import static ua.corporation.memeclimb.config.Markup.HOW_IT_WORKS_BUTTONS;

public class HowItWorks extends Action implements Callback {
    public static final String KEY = "instruction";

    public HowItWorks(Internationalization internationalization) {
        super(internationalization, null, null, null);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = internationalization.getLocalizationMessage(KEY);

        return callback(chatId, text);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .replyMarkup(HOW_IT_WORKS_BUTTONS.get(internationalization));
    }
}
