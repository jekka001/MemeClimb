package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;

import java.util.List;

import static ua.corporation.memeclimb.config.Markup.BALANCE_BUTTONS;
import static ua.corporation.memeclimb.config.ReplayText.BALANCE;

public class Check extends Action implements Callback {
    public static final String KEY = "check";

    public Check(Internationalization internationalization, BalanceService balanceService) {
        super(internationalization, null, null, balanceService);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = prepareText(user);

        return callback(chatId, text);
    }

    private String prepareText(UserDto user) {
        String text = internationalization.getLocalizationMessage(KEY);
        String balance = balanceService.showBalance(user);

        return text.replace(BALANCE.getKey(), balance);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .replyMarkup(BALANCE_BUTTONS.get(internationalization));
    }
}
