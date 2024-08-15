package ua.corporation.memeclimb.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.corporation.memeclimb.exception.EmptyBalanceException;
import ua.corporation.memeclimb.exception.PayPrizeException;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.TelegramCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.corporation.memeclimb.config.Markup.PROBLEM_WITH_BALANCE;
import static ua.corporation.memeclimb.config.Markup.START_BUTTONS;

@Service
public class TelegramBotListener implements UpdatesListener {

    private final TelegramBot memeClimbBot;
    private final TelegramCreator creator;
    private final Map<Long, List<Integer>> chatIdToMessageId = new HashMap<>();
    private final Internationalization internationalization = new Internationalization();


    public TelegramBotListener(@Autowired TelegramCreator creator,
                               @Value("${memeClimbBotToken}") String propertyBotToken) {
        this.creator = creator;
        memeClimbBot = new TelegramBot(propertyBotToken);
        memeClimbBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::update);

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void update(Update update) {
        if (update.myChatMember() == null) {
            boolean isMessage = update.message() != null;
            long chatId = getChatId(update, isMessage);

            prepareChat(update, isMessage, chatId);
            Thread thread = new Thread(() -> makeAnswer(update, isMessage, chatId));
            thread.start();
        }

        //TODO write myChatMember process
    }

    private void makeAnswer(Update update, boolean isMessage, long chatId) {
        Message messageTemp = addProcessingMessage(update, isMessage, chatId);

        try {
            sendAnswer(update, isMessage, chatId);
        } catch (EmptyBalanceException emptyBalance) {
            Message message = memeClimbBot.execute(balanceEmpty(emptyBalance)).message();
            saveShownMessage(message.chat().id(), message.messageId());
        } catch (PayPrizeException payEx) {
            String error = "Sorry, we couldn't send your prize, try later or contact with our support " +
                    "(for last spin we didn't take pay)";
            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
            saveShownMessage(message.chat().id(), message.messageId());
        } catch (RuntimeException e) {
            String error = "Heeee wats this? AAAA waaaaats thiiiis heeee:)";
            System.out.println(e.getMessage());
            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
            saveShownMessage(message.chat().id(), message.messageId());
        }

        deleteProcessingMessage(update, isMessage, chatId, messageTemp);
        Thread.currentThread().interrupt();
    }

    private Message addProcessingMessage(Update update, boolean isMessage, long chatId) {
        if (creator.isSpinAction(update, isMessage)) {
            SendMessage sendMessage = new SendMessage(chatId, "Processing")
                    .parseMode(ParseMode.HTML);
            Message messageTemp = memeClimbBot.execute(sendMessage).message();
            saveShownMessage(messageTemp.chat().id(), messageTemp.messageId());
            return messageTemp;
        }

        return null;
    }

    private void deleteProcessingMessage(Update update, boolean isMessage, long chatId, Message messageTemp) {
        if (messageTemp != null && creator.isSpinAction(update, isMessage)) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, messageTemp.messageId());
            memeClimbBot.execute(deleteMessage);
        }
    }

    private long getChatId(Update update, boolean isMessage) {
        return isMessage ?
                update.message().chat().id() :
                update.callbackQuery().from().id();
    }

    private void prepareChat(Update update, boolean isMessage, long chatId) {
        int messageId = getMessageId(update, isMessage);

        saveShownMessage(chatId, messageId);
        clearChat(chatId);
    }

    private int getMessageId(Update update, boolean isMessage) {
        return isMessage ?
                update.message().messageId() :
                update.callbackQuery().message().messageId();
    }

    private void sendAnswer(Update update, boolean isMessage, long chatId) {
        List<SendMessage> sendMessages = creator.createSendMessages(update, isMessage, chatId, internationalization);

        sendMessages.forEach(sendMessage -> {
            Message message = memeClimbBot.execute(sendMessage).message();
            saveShownMessage(message.chat().id(), message.messageId());
        });
    }

    private void clearChat(long chatId) {
        chatIdToMessageId.get(chatId).forEach(id -> {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, id);
            memeClimbBot.execute(deleteMessage);
        });
    }

    private void saveShownMessage(long chatId, int messageId) {
        if (chatIdToMessageId.containsKey(chatId)) {
            chatIdToMessageId.get(chatId).add(messageId);
        } else {
            List<Integer> messageIds = new ArrayList<>(10);
            messageIds.add(messageId);
            chatIdToMessageId.put(chatId, messageIds);
        }
    }


    public SendMessage balanceEmpty(EmptyBalanceException emptyBalanceException) {
        return new SendMessage(emptyBalanceException.getChatId(), "Sorry your balance so small)")
                .parseMode(ParseMode.HTML)
                .replyMarkup(PROBLEM_WITH_BALANCE.get(internationalization));
    }

    public SendMessage bedCommand(long chatId, String exception) {
        return new SendMessage(chatId, exception)
                .parseMode(ParseMode.HTML)
                .replyMarkup(START_BUTTONS.get(internationalization));
    }
}
