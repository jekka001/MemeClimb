package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;

import java.util.List;

import static ua.corporation.memeclimb.config.Markup.WITHDRAW_BUTTONS;

public class Withdraw extends Action implements Callback {
    public static final String KEY = "withdraw";

    public Withdraw(Internationalization internationalization, BalanceService balanceService) {
        super(internationalization, null, null, balanceService);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = internationalization.getLocalizationMessage(KEY);

        return callback(chatId, text);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .replyMarkup(WITHDRAW_BUTTONS.get(internationalization));
    }
}
