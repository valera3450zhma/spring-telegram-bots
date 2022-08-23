package springbot.deputat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import springbot.deputat.config.BotConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeputatBot extends TelegramLongPollingBot {

    private final BotConfig config;


    public DeputatBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            try {
                execute(new SendMessage(String.valueOf(chatId), messageText));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

}