package ua.corporation.memeclimb.creator;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.action.callback.*;
import ua.corporation.memeclimb.entity.action.message.Authority;
import ua.corporation.memeclimb.entity.action.message.Start;
import ua.corporation.memeclimb.entity.action.message.WithdrawSend;
import ua.corporation.memeclimb.entity.main.MemeMessage;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.BalanceService;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.TelegramCreator;
import ua.corporation.memeclimb.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramSendMessageCreator implements TelegramCreator {
    private final UserService userService;
    private final PoolService poolService;
    private final BalanceService balanceService;
    private Internationalization internationalization;

    @Override
    public List<SendMessage> createSendMessages(MemeMessage memeMessage, Internationalization internationalization) {
        this.internationalization = internationalization;
        UserDto user = userService.get(memeMessage.getTelegramUser());
        Action action = createAction(memeMessage.getData());

        return action.generate(memeMessage.getChatId(), user);
    }

    @Override
    public boolean isSpinAction(MemeMessage memeMessage) {
        String data = memeMessage.getData();

        List<String> spinAction = new ArrayList<>();
        spinAction.add(Lose.KEY);
        spinAction.add(Win.KEY);
        spinAction.add(AllSteps.KEY);

        return spinAction.contains(data);
    }

    private Action createAction(String actionKey) {
        return switch (actionKey) {
            case Start.COMMAND -> new Start(internationalization);
            case Pools.KEY -> new Pools(internationalization, poolService, userService, 0);
            case TopUp.KEY -> new TopUp(internationalization, userService, balanceService);
            case Check.KEY -> new Check(internationalization, balanceService);
            case Pools.SECOND_KEY -> new Pools(internationalization, poolService, userService, 1);
            case Withdraw.KEY -> new Withdraw(internationalization, balanceService);
            case Lose.KEY -> new Lose(internationalization, poolService, userService, balanceService);
            case Win.KEY -> new Win(internationalization, poolService, userService, balanceService);
            case AllSteps.KEY -> new AllSteps(internationalization, poolService, userService, balanceService);
            case HowItWorks.KEY -> new HowItWorks(internationalization);
            case Support.KEY -> new Support(internationalization);
            case EarnMore.KEY -> new EarnMore(internationalization);
            default -> chooseActionWithoutKey(actionKey);
        };
    }

    private Action chooseActionWithoutKey(String actionKey) {
        if (actionKey.contains(ChoosePool.KEY)) {
            return new ChoosePool(internationalization, poolService, userService, balanceService, actionKey);
        } else if (WithdrawSend.isWalletAddress(actionKey)) {
            return new WithdrawSend(internationalization, balanceService, actionKey);
        } else if (Authority.checkAuthority(actionKey)) {
            return new Authority(internationalization, poolService, userService);
        }

        throw new RuntimeException("I don't know what is the action");
    }
}
