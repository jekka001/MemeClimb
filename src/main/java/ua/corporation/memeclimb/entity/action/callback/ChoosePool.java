package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.comparator.StepOrder;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.Step;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

import static ua.corporation.memeclimb.config.Button.BACK;
import static ua.corporation.memeclimb.config.Button.TOP_UP;
import static ua.corporation.memeclimb.config.ReplayText.*;
import static ua.corporation.memeclimb.entity.action.callback.Pools.POOL_INFO;

public class ChoosePool extends Action implements Callback {
    public static final String KEY = "choosePool";
    private final String text;
    private UserDto user;
    private PoolDto pool;

    public ChoosePool(Internationalization internationalization, PoolService poolService, UserService userService, BalanceService balanceService, String text) {
        super(internationalization, poolService, userService, balanceService);
        this.text = text;
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto userDto) {
        this.user = prepareUser(userDto);
        this.pool = poolService.getPool(user.getChosenPoolId());

        String text = prepareText();

        return callback(chatId, text);
    }

    private UserDto prepareUser(UserDto user) {
        String poolId = text.replace(KEY, "");

        return userService.saveChosenPool(poolId, user);
    }

    private String prepareText() {
        String text = internationalization.getLocalizationMessage(KEY);
        int completedStep = poolService.getUserStep(user);
        String stepInfo = getStepInfo(completedStep, pool);

        return text
                .replace(POOL.getKey(), displayPool(pool, completedStep))
                .replace(STEP.getKey(), stepInfo)
                .replace(COUNT_OF_STEP.getKey(), String.valueOf(pool.getSteps().size()))
                .replace(INITIAL_FEE.getKey(), String.valueOf(pool.getInitialFee()));
    }

    public String displayPool(PoolDto poolDto, int countCompletedSteps) {
        String text = internationalization.getLocalizationMessage(POOL_INFO);

        text = text.replace(POOL_NAME.getKey(), poolDto.getName());
        text = text.replace(TOP_REWARD.getKey(), poolDto.getStringTopReward());
        text = text.replace(POOL_REWARD.getKey(), generateTextPoolReward(poolDto));
        text = text.replace(PARTICIPANT.getKey(), String.valueOf(poolDto.getParticipants().size()));
        text = text.replace(WINNER.getKey(), String.valueOf(poolDto.getWinners().size()));
        text = text.replace(COMPLETED_STEPS.getKey(), String.valueOf(countCompletedSteps));
        text = text.replace(COUNT_OF_STEP.getKey(), String.valueOf(poolDto.getSteps().size()));

        return text;
    }

    private String getStepInfo(int completedStep, PoolDto chosenPool) {
        if (chosenPool.getSteps().size() != 0) {
            List<Step> steps = chosenPool.getSteps();
            steps.sort(new StepOrder());
            return steps.get(completedStep).displayStep();
        } else {
            return "problem with step";
        }
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        InlineKeyboardMarkup choosePoolButtons = prepareMarkup();

        return new SendMessage(chatId, text)
                .replyMarkup(choosePoolButtons)
                .parseMode(ParseMode.HTML);
    }

    private InlineKeyboardMarkup prepareMarkup() {
        InlineKeyboardButton spin = getSpinButton(pool, user, false);
        InlineKeyboardButton back = BACK.getButton(internationalization);
        InlineKeyboardButton topUp = TOP_UP.getButton(internationalization);

        return new InlineKeyboardMarkup().addRow(spin).addRow(back).addRow(topUp);
    }
}
