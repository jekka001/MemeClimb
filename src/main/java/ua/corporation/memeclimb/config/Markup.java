package ua.corporation.memeclimb.config;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ua.corporation.memeclimb.lang.Internationalization;

import java.util.Arrays;
import java.util.List;

import static ua.corporation.memeclimb.config.Button.*;

public enum Markup {
    START_BUTTONS(POOLS, TOP_UP, CHECK, HOW_IT_WORKS),
    TOP_UP_BUTTONS(CHECK, POOLS),
    BALANCE_BUTTONS(POOLS, TOP_UP, WITHDRAW_BALANCE, SUPPORT),
    WIN_BUTTONS(CHOOSE_YOUR_NEXT_POOL, CHECK, EARN_MORE),
    WITHDRAW_BUTTONS(POOLS, CHECK, SUPPORT),
    HOW_IT_WORKS_BUTTONS(POOLS, CHECK, SUPPORT),
    SUPPORT_BUTTONS(POOLS, CHECK, SUPPORT),
    EARN_MORE_BUTTONS(POOLS, CHECK, SUPPORT),
    PROBLEM_WITH_BALANCE(TOP_UP, POOLS, EARN_MORE);

    private final List<Button> buttons;

    Markup(Button... buttons) {
        this.buttons = Arrays.asList(buttons);
    }

    public InlineKeyboardMarkup get(Internationalization internationalization) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        buttons.forEach(button -> inlineKeyboardMarkup.addRow(button.getButton(internationalization)));

        return inlineKeyboardMarkup;
    }
}
