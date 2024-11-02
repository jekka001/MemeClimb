package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.comparator.StepOrder;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.Step;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

import static ua.corporation.memeclimb.config.Button.*;
import static ua.corporation.memeclimb.config.ReplayText.*;


public class Lose extends Action implements Callback {
    public static final SpinState SPIN_STATE = SpinState.LOSE;
    public static final String KEY = "lose";
    private UserDto user;
    private PoolDto pool;
    private final String text;

    public Lose(Internationalization internationalization, PoolService poolService, UserService userService,
                BalanceService balanceService, String text) {
        super(internationalization, poolService, userService, balanceService);
        this.text = text;
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto userDto) {
        this.user = prepareUser(userDto, text, KEY);
        saveSpinResult(user, 0, SPIN_STATE);
        this.pool = poolService.getPool(user.getChosenPoolId());

        String text = prepareText();

        return callback(chatId, text);
    }

    private String prepareText() {
        int stepNumber = poolService.getUserStep(pool, user) - 1;
        String text = internationalization.getLocalizationMessage(KEY);
        List<Step> steps = pool.getSteps();
        steps.sort(new StepOrder());

        text = text.replace(STEP.getKey(), steps.get(stepNumber).displayCurrentStep());
        text = text.replace(TOP_REWARD.getKey(), pool.getStringTopReward());
        text = text.replace(TIME.getKey(), String.valueOf(48));
        text = text.replace(POOL_REWARD.getKey(), generateTextPoolReward(pool));

        return text;
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        InlineKeyboardMarkup loseButtons = prepareMarkup();

        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(loseButtons);
    }

    private InlineKeyboardMarkup prepareMarkup() {
        InlineKeyboardButton spin = getSpinButton(pool, user, true);
        InlineKeyboardButton chooseNextPool = CHOOSE_YOUR_NEXT_POOL.getButton(internationalization);
        InlineKeyboardButton topUp = TOP_UP.getButton(internationalization);
        InlineKeyboardButton earnMore = EARN_MORE.getButton(internationalization);

        return new InlineKeyboardMarkup().addRow(spin).addRow(chooseNextPool).addRow(topUp).addRow(earnMore);
    }
}
