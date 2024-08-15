package ua.corporation.memeclimb.entity.action.message;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.config.Markup;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;

public class Authority extends Action implements Message {
    private static final String KEY = "authority";

    public Authority(Internationalization internationalization, PoolService poolService, UserService userService) {
        super(internationalization, poolService, userService, null);
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        if (user.getTelegramId().equals("362171503") && user.getName().equals("prostoIaToot")) {
            poolService.delete();
            userService.delete();
        }

        return message(chatId, "try to delete");
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(Markup.WITHDRAW_BUTTONS.get(internationalization));
    }

    public static boolean checkAuthority(String information) {
        return information.equals("I don't like it");
    }
}
