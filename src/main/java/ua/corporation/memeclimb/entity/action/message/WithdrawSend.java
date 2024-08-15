package ua.corporation.memeclimb.entity.action.message;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.config.Markup;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithdrawSend extends Action implements Message {
    private static final String KEY = "withdrawSend";
    private static final String REGEX = "^\\w{43,46}$";
    private final String publicKey;

    public WithdrawSend(Internationalization internationalization, BalanceService balanceService, String publicKey) {
        super(internationalization, null, null, balanceService);
        this.publicKey = publicKey;
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        String text = internationalization.getLocalizationMessage(KEY);

        withdraw(user, publicKey);

        return message(chatId, text);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(Markup.WITHDRAW_BUTTONS.get(internationalization));
    }

    public static boolean isWalletAddress(String walletAddress) {
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(walletAddress);

        return matcher.find();
    }
}
