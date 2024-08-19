package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

import static ua.corporation.memeclimb.config.Button.*;
import static ua.corporation.memeclimb.config.ReplayText.POOL_NAME;
import static ua.corporation.memeclimb.config.ReplayText.TOP_REWARD;

public class AllSteps extends Action implements Callback {
    public static final SpinState SPIN_STATE = SpinState.ALL_STEPS;
    public static final String KEY = "allSteps";
    private UserDto user;
    private PoolDto pool;
    private CoinDto coinDto;

    public AllSteps(Internationalization internationalization, PoolService poolService, UserService userService, BalanceService balanceService) {
        super(internationalization, poolService, userService, balanceService);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        this.user = user;
        this.pool = poolService.getPool(user.getChosenPoolId());
        coinDto = saveSpinResult(user, 0, SPIN_STATE);

        String text = prepareText();

        return callback(chatId, text);
    }

    private String prepareText() {
        String text = internationalization.getLocalizationMessage(KEY);

        return text.replace(POOL_NAME.getKey(), pool.getName())
                .replace(TOP_REWARD.getKey(), coinDto.getAmount() + " " + coinDto.getSymbol());
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        InlineKeyboardMarkup allStepsButtons = prepareMarkup();

        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(allStepsButtons);
    }

    private InlineKeyboardMarkup prepareMarkup() {
        InlineKeyboardButton spin = getSpinButton(pool, user, true);
        InlineKeyboardButton chooseNextPool = CHOOSE_YOUR_NEXT_POOL.getButton(internationalization);
        InlineKeyboardButton checkBalance = CHECK.getButton(internationalization);
        InlineKeyboardButton earnMore = EARN_MORE.getButton(internationalization);

        return new InlineKeyboardMarkup().addRow(spin).addRow(chooseNextPool).addRow(checkBalance).addRow(earnMore);
    }
}
