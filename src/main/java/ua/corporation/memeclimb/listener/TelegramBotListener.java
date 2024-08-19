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
import ua.corporation.memeclimb.entity.main.MemeMessage;
import ua.corporation.memeclimb.exception.*;
import ua.corporation.memeclimb.lang.Internationalization;
import ua.corporation.memeclimb.service.TelegramCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ua.corporation.memeclimb.config.Markup.PROBLEM_WITH_BALANCE;
import static ua.corporation.memeclimb.config.Markup.START_BUTTONS;

@Service
public class TelegramBotListener implements UpdatesListener {

    private final TelegramBot memeClimbBot;
    private final TelegramCreator creator;
    private final List<MemeMessage> memeMessages = new ArrayList<>();
    private final Internationalization internationalization = new Internationalization();
    private final ExecutorService threadExecutor = Executors.newFixedThreadPool(100);


    public TelegramBotListener(@Autowired TelegramCreator creator,
                               @Value("${memeClimbBotToken}") String propertyBotToken) {
        this.creator = creator;
        memeClimbBot = new TelegramBot(propertyBotToken);
        memeClimbBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::createAnswer);

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void createAnswer(Update update) {
        if (update.myChatMember() == null) {
            MemeMessage memeMessage = new MemeMessage(update);
            if (!memeMessages.contains(memeMessage)) {
                prepareChat(memeMessage);
                threadExecutor.execute(() -> makeAnswer(memeMessage));
            }
        }
    }

    private void prepareChat(MemeMessage memeMessage) {
        clearChat(memeMessage);
        memeMessages.add(memeMessage);
    }

    private void clearChat(MemeMessage memeMessage) {
        long chatId = memeMessage.getChatId();

        memeMessages.stream()
                .filter(message -> message.getChatId() == chatId)
                .forEach(this::executeDeleteMessage);
    }

    private void executeDeleteMessage(MemeMessage message) {
        DeleteMessage deleteMessage = new DeleteMessage(message.getChatId(), message.getMessageId());
        memeClimbBot.execute(deleteMessage);
    }

    private void makeAnswer(MemeMessage memeMessage) {
        MemeMessage processingMessage = sendMessageBeforeAnswer(memeMessage);

        try {
            sendAnswer(memeMessage);
        } catch (Throwable ex) {
            handleException(ex, memeMessage.getChatId());
        }

        deleteMessageBeforeAnswer(processingMessage);
    }

    private MemeMessage sendMessageBeforeAnswer(MemeMessage memeMessage) {
        if (creator.isSpinAction(memeMessage)) {
            SendMessage sendMessage = new SendMessage(memeMessage.getChatId(), "Processing")
                    .parseMode(ParseMode.HTML);
            Message messageTemp = memeClimbBot.execute(sendMessage).message();

            MemeMessage result = new MemeMessage(messageTemp);
            memeMessages.add(result);

            return result;
        }

        return null;
    }

    private void sendAnswer(MemeMessage memeMessage) {
        List<SendMessage> sendMessages = creator.createSendMessages(memeMessage, internationalization);

        sendMessages.forEach(sendMessage -> {
            Message message = memeClimbBot.execute(sendMessage).message();
            memeMessages.add(new MemeMessage(message));
        });
    }

    private void deleteMessageBeforeAnswer(MemeMessage memeMessage) {
        if (memeMessage != null && creator.isSpinAction(memeMessage)) {
            DeleteMessage deleteMessage = new DeleteMessage(memeMessage.getChatId(), memeMessage.getMessageId());
            memeClimbBot.execute(deleteMessage);
        }
    }

    private void handleException(Throwable ex, long chatId) {
        if (ex.getClass() == ToManyRequestException.class) {
//            ToManyRequestException exception = (ToManyRequestException) ex;
//            String error = exception.getMessage();
//            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
//            memeMessages.add(new MemeMessage(message));
        } else if (ex.getClass() == EmptyBalanceException.class) {
            Message message = memeClimbBot.execute(balanceEmpty(chatId)).message();
            memeMessages.add(new MemeMessage(message));
        } else if (ex.getClass() == PayPrizeException.class) {
            String error = "Sorry, we couldn't send your prize, try later or contact with our support " +
                    "(for last spin we didn't take pay)";
            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
            memeMessages.add(new MemeMessage(message));
        } else if (ex.getClass() == ServerProblemException.class) {
            String error = "Sorry, some problem with server, please try again";
            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
            memeMessages.add(new MemeMessage(message));
        } else if (ex.getClass() == NotExpectedException.class) {
            String error = "Heeee wats this? AAAA waaaaats thiiiis heeee:)";
            Message message = memeClimbBot.execute(bedCommand(chatId, error)).message();
            memeMessages.add(new MemeMessage(message));
        }
    }

    public SendMessage balanceEmpty(long chatId) {
        return new SendMessage(chatId, "Sorry your balance so small)")
                .parseMode(ParseMode.HTML)
                .replyMarkup(PROBLEM_WITH_BALANCE.get(internationalization));
    }

    public SendMessage bedCommand(long chatId, String exception) {
        return new SendMessage(chatId, exception)
                .parseMode(ParseMode.HTML)
                .replyMarkup(START_BUTTONS.get(internationalization));
    }
}
