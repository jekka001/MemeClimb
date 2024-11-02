package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.PoolCoin;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

import static ua.corporation.memeclimb.config.Markup.WIN_BUTTONS;
import static ua.corporation.memeclimb.config.ReplayText.POOL_NAME;
import static ua.corporation.memeclimb.config.ReplayText.TOP_REWARD;

public class Win extends Action implements Callback {
    public static final SpinState SPIN_STATE = SpinState.WIN;
    public static final String KEY = "win";
    private PoolDto pool;
    private final String text;

    public Win(Internationalization internationalization, PoolService poolService, UserService userService,
               BalanceService balanceService, String text) {
        super(internationalization, poolService, userService, balanceService);
        this.text = text;
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto userDto) {
        UserDto user = prepareUser(userDto, text, KEY);
        this.pool = poolService.getPool(user.getChosenPoolId());
        PoolCoin poolCoin = pool.getTopReward();

        saveSpinResult(user, poolCoin.getUsdPrize(), SPIN_STATE);
        poolService.saveWinner(user);

        String text = prepareText();

        return callback(chatId, text);
    }

    @NotNull
    private String prepareText() {
        String topReward = pool.getStringTopReward();
        String text = internationalization.getLocalizationMessage(KEY);

        text = text.replace(TOP_REWARD.getKey(), topReward);
        text = text.replace(POOL_NAME.getKey(), pool.getName());

        return text;
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(WIN_BUTTONS.get(internationalization));
    }
}
