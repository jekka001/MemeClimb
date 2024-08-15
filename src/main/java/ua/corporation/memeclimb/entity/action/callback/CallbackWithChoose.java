package ua.corporation.memeclimb.entity.action.callback;

import com.pengrad.telegrambot.request.SendMessage;
import ua.corporation.memeclimb.entity.main.Pool;
import ua.corporation.memeclimb.entity.main.dto.PoolDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CallbackWithChoose extends Callback {
    default List<SendMessage> callback(long chatId, Map<PoolDto, String> poolToText) {
        List<SendMessage> sendMessages = new ArrayList<>();
        int counter = 0;

        for (Map.Entry<PoolDto, String> entry : poolToText.entrySet()) {
            counter++;

            SendMessage sendMessage =
                    createMessageWithChooseButton(chatId, entry.getValue(), entry.getKey(), counter == poolToText.size());
            sendMessages.add(sendMessage);
        }

        return sendMessages;
    }

    SendMessage createMessageWithChooseButton(Long chatId, String text, PoolDto pool, boolean lastStep);

}
