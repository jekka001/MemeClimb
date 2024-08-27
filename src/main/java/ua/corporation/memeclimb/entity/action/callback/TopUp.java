package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

import static ua.corporation.memeclimb.config.Markup.TOP_UP_BUTTONS;
import static ua.corporation.memeclimb.config.ReplayText.CLIENT_PUBLIC_KEY;

public class TopUp extends Action implements Callback {
    public static final String KEY = "topUp";

    public TopUp(Internationalization internationalization, UserService userService, BalanceService balanceService) {
        super(internationalization, null, userService, balanceService);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = prepareText(user, internationalization);

        balanceService.addVirtualMoney(user, 1000.0);

        return callback(chatId, text);
    }

    @NotNull
    private String prepareText(UserDto user, Internationalization internationalization) {
        String text = internationalization.getLocalizationMessage(KEY);

        String userPublicKeyString = "<b><code>" + user.getPublicKey() + "</code></b>";

        text = text.replace(CLIENT_PUBLIC_KEY.getKey(), userPublicKeyString);

        return text;
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(TOP_UP_BUTTONS.get(internationalization));
    }
}
