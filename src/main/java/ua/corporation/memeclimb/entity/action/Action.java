package ua.corporation.memeclimb.entity.action;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.transaction.annotation.Transactional;
import ua.corporation.memeclimb.entity.action.callback.AllSteps;
import ua.corporation.memeclimb.entity.action.callback.Lose;
import ua.corporation.memeclimb.entity.action.callback.Win;
import ua.corporation.memeclimb.entity.main.SpinState;
import ua.corporation.memeclimb.entity.main.dto.CoinDto;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.exception.EmptyBalanceException;
import ua.corporation.memeclimb.lang.ButtonText;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import static ua.corporation.memeclimb.config.ReplayText.SPIN_FEE;
import static ua.corporation.memeclimb.lang.ButtonText.SPIN_NEXT;

public abstract class Action {
    protected final Internationalization internationalization;
    protected final PoolService poolService;
    protected final UserService userService;
    protected final BalanceService balanceService;

    private final Random random = new Random();

    protected Action(Internationalization internationalization, PoolService poolService, UserService userService, BalanceService balanceService) {
        this.internationalization = internationalization;
        this.poolService = poolService;
        this.userService = userService;
        this.balanceService = balanceService;
    }

    private Action() {
        throw new RuntimeException("Without realization");
    }

    public abstract List<SendMessage> generate(long chatId, UserDto user);

    protected abstract SendMessage createSendMessage(Long chatId, String text);

    protected CoinDto saveSpinResult(UserDto user, double prize, SpinState spinState) {
        double fee = getFee(user).doubleValue();

        if (balanceService.checkBalance(user, fee)) {
            if (spinState.equals(SpinState.ALL_STEPS)) {
                prize = calculatePrize(user, fee);
            }
            CoinDto coinDto = calculateBalance(user, prize, spinState, fee);
            calculateStep(user);
            saveSpentMoney(user, fee);
            if (spinState.equals(SpinState.ALL_STEPS)) {
                clearSpentMoney(user);
            } else if (spinState.equals(SpinState.WIN)) {
                clearSpentMoney(user);
            }
            return coinDto;
        } else {
            throw new EmptyBalanceException("Balance empty");
        }
    }

    private void saveSpentMoney(UserDto user, double fee) {
        PoolDto pool = poolService.getPool(user.getChosenPoolId());
        ParticipantDto participant = poolService.getParticipant(pool, user);

        participant.spend(fee);

        poolService.saveParticipant(participant);
    }

    private void clearSpentMoney(UserDto user) {
        PoolDto pool = poolService.getPool(user.getChosenPoolId());
        ParticipantDto participant = poolService.getParticipant(pool, user);

        participant.setSpendMoney(0);
        participant.setUserStep(0);

        poolService.saveParticipant(participant);
    }

    private double calculatePrize(UserDto user, Double fee) {
        PoolDto pool = poolService.getPool(user.getChosenPoolId());
        ParticipantDto participant = poolService.getParticipant(pool, user);

        return (participant.getSpendMoney() + fee) * 0.5;
    }

    @Transactional
    public CoinDto calculateBalance(UserDto user, double addPrize, SpinState spinState, double fee) {
        if (addPrize != 0) {
            balanceService.subtractFee(user, fee);
            return balanceService.addPrize(user, addPrize, spinState);
        }

        balanceService.subtractFee(user, fee);

        return new CoinDto();
    }


    private void calculateStep(UserDto user) {
        poolService.addStep(user);
    }

    protected InlineKeyboardButton getSpinButton(PoolDto pool, UserDto user, boolean isNextSpin) {
        String spinButtonText = isNextSpin ? SPIN_NEXT.getKey(internationalization) : getSpinButtonText(pool, user);
        int allSteps = pool.getSteps().size();
        int userStep = poolService.getUserStep(user);

        BigDecimal fee = getFee(user);
        boolean winResult = getResult(user);

        spinButtonText = spinButtonText.replace(SPIN_FEE.getKey(), fee.toString());

        return createSpinButton(spinButtonText, allSteps, userStep, winResult);
    }

    private boolean getResult(UserDto user) {
        PoolDto chosenPool = poolService.getPool(user.getChosenPoolId());

        double ratioProbability = chosenPool.getRatioProbability();
        int allSteps = chosenPool.getSteps().size();
        double probabilityWin = chosenPool.getProbabilityWin();
        int userStep = poolService.getUserStep(user);

        return calculateSpinResult(ratioProbability, allSteps, probabilityWin, userStep);
    }

    private boolean calculateSpinResult(double ratioProbability, int allSteps, double probabilityWin, int userStep) {
        int randomNumber = random.nextInt(101);

        double divided = 1 - Math.pow(ratioProbability, allSteps);
        double divisor = 1 - ratioProbability;
        double divisor2 = (divided / divisor);

        double win = probabilityWin / 100;
        double firstStepProb = win / divisor2;

        double chanceRatio = firstStepProb * Math.pow(ratioProbability, userStep);

        return randomNumber > (100 - (chanceRatio * 100));
    }

    private InlineKeyboardButton createSpinButton(String spinText, int allSteps, int userStep, boolean winResult) {
        if (winResult) {
            return new InlineKeyboardButton(spinText).callbackData(Win.KEY);
        } else if (allSteps == userStep + 1) {
            return new InlineKeyboardButton(spinText).callbackData(AllSteps.KEY);
        } else {
            return new InlineKeyboardButton(spinText).callbackData(Lose.KEY);
        }
    }

    private BigDecimal getFee(UserDto user) {
        PoolDto chosenPool = poolService.getPool(user.getChosenPoolId());

        double initialFee = chosenPool.getInitialFee();
        double ratioProbability = chosenPool.getRatioProbability();
        int userStep = poolService.getUserStep(user);

        return calculateFee(initialFee, ratioProbability, userStep);
    }

    private BigDecimal calculateFee(double initialFee, double ratioProbability, int userStep) {
        MathContext context = new MathContext(4, RoundingMode.HALF_UP);

        double fee = initialFee * Math.pow(ratioProbability, userStep);

        return new BigDecimal(fee, context);
    }

    private String getSpinButtonText(PoolDto pool, UserDto user) {
        boolean isFirstly = poolService.getParticipant(pool, user).isFirstly();

        return isFirstly ?
                ButtonText.FIRST_SPIN.getKey(internationalization) :
                ButtonText.SPIN.getKey(internationalization);
    }

    protected void withdraw(UserDto userDto, String withdrawWallet) {
        balanceService.withdraw(userDto, withdrawWallet);
    }


    public String generateTextPoolReward(PoolDto pool) {
        List<String> coinsSymbol = pool.getPoolCoins().stream()
                .filter(poolCoin -> !poolCoin.isTopReward())
                .map(poolCoin -> poolCoin.getCoin().getSymbol()).toList();

        StringBuilder result = new StringBuilder();

        for (int counter = 0; counter < coinsSymbol.size(); counter++) {
            result.append(coinsSymbol.get(counter));
            if (counter != coinsSymbol.size() - 1) {
                result.append(", ");
            }
        }

        return result.toString();
    }
}
