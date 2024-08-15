package ua.corporation.memeclimb.config;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ua.corporation.memeclimb.entity.action.callback.*;
import ua.corporation.memeclimb.lang.ButtonText;
import ua.corporation.memeclimb.lang.Internationalization;

public enum Button {
    POOLS(ButtonText.REVEAL_POOLS, Pools.KEY),
    TOP_UP(ButtonText.TOP_UP_BALANCE, TopUp.KEY),
    CHECK(ButtonText.CHECK_BALANCE, Check.KEY),
    HOW_IT_WORKS(ButtonText.HOW_IT_WORKS, HowItWorks.KEY),
    WITHDRAW_BALANCE(ButtonText.WITHDRAW, Withdraw.KEY),
    SUPPORT(ButtonText.SUPPORT, Support.KEY),
    MORE_POOLS(ButtonText.MORE_POOLS, Pools.SECOND_KEY),
    BACK(ButtonText.BACK, Pools.KEY),
    CHOOSE_YOUR_NEXT_POOL(ButtonText.CHOSE_NEXT_POOL, Pools.KEY),
    EARN_MORE(ButtonText.EARN_MORE, EarnMore.KEY);

    private final ButtonText buttonText;
    private final String callback;

    Button(ButtonText buttonText, String callback) {
        this.buttonText = buttonText;
        this.callback = callback;
    }

    public InlineKeyboardButton getButton(Internationalization internationalization) {
        String text = buttonText.getKey(internationalization);

        return new InlineKeyboardButton(text).callbackData(callback);
    }
}
