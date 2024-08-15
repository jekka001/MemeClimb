package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.comparator.PoolData;
import ua.corporation.memeclimb.entity.action.Action;
import ua.corporation.memeclimb.entity.main.dto.ParticipantDto;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;
import ua.corporation.memeclimb.entity.main.dto.UserDto;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.PoolService;
import ua.corporation.memeclimb.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ua.corporation.memeclimb.config.Button.*;
import static ua.corporation.memeclimb.lang.ButtonText.REVEAL_POOL;

public class Pools extends Action implements CallbackWithChoose {
    public static final String KEY = "pool";
    public static final String SECOND_KEY = "morePools";
    private final int page;
    private UserDto user;

    public Pools(Internationalization internationalization, PoolService poolService, UserService userService, int page) {
        super(internationalization, poolService, userService, null);
        this.page = page;
    }

    @Override
    public List<SendMessage> generate(long chatId, UserDto user) {
        this.user = prepareUser(user);
        Map<PoolDto, String> poolToMessage = createMap();

        return callback(chatId, poolToMessage);
    }

    private UserDto prepareUser(UserDto user) {
        boolean isPoolExist = poolService.isPoolExist(user.getPage() + page);

        int newUserPage = isPoolExist ? (user.getPage() + page) : 0;
        user.setPage(newUserPage);

        return userService.save(user);
    }

    private Map<PoolDto, String> createMap() {
        Map<PoolDto, String> poolToMessage = new TreeMap<>(new PoolData());
        List<PoolDto> pools = poolService.getPools(user.getPage());

        for (int counter = 0; counter < pools.size(); counter++) {
            String readyMessage = getReadyMessage(user, pools, counter);

            poolToMessage.put(pools.get(counter), readyMessage);
        }

        return poolToMessage;
    }

    private String getReadyMessage(UserDto user, List<PoolDto> pools, int counter) {
        String text = internationalization.getLocalizationMessage(KEY + counter);

        ParticipantDto foundParticipant = poolService.getParticipant(pools.get(counter), user);

        return text.replace("<" + counter + ">", pools.get(counter).displayPool(foundParticipant.getUserStep()));
    }

    @Override
    public SendMessage createMessageWithChooseButton(Long chatId, String text, PoolDto pool, boolean lastPool) {
        InlineKeyboardMarkup revealPool = prepareMarkup(pool, lastPool);

        return createSendMessage(chatId, text)
                .replyMarkup(revealPool);
    }

    private InlineKeyboardMarkup prepareMarkup(PoolDto pool, boolean lastPool) {
        String revealPool = REVEAL_POOL.getKey(internationalization);
        InlineKeyboardButton revealPoolButton = new InlineKeyboardButton(revealPool).callbackData(ChoosePool.KEY + pool.getId());

        return lastPool ?
                getMarkupForLastPool(revealPoolButton) :
                new InlineKeyboardMarkup().addRow(revealPoolButton);
    }

    private InlineKeyboardMarkup getMarkupForLastPool(InlineKeyboardButton revealPoolButton) {
        InlineKeyboardButton morePools = MORE_POOLS.getButton(internationalization);
        InlineKeyboardButton topUp = TOP_UP.getButton(internationalization);
        InlineKeyboardButton howItWorks = HOW_IT_WORKS.getButton(internationalization);

        return new InlineKeyboardMarkup().addRow(revealPoolButton).addRow(morePools).addRow(topUp).addRow(howItWorks);
    }

    @Override
    public SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML);
    }
}
