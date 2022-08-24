package springbot.deputat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import springbot.deputat.config.BotConfig;
import springbot.deputat.executable.DeputatExeContainer;
import springbot.telegram.ExecutableNotFoundException;
import springbot.telegram.UpdateProcessor;

@Slf4j
@Component
public class DeputatBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final DeputatExeContainer executables;

    public DeputatBot(BotConfig config, @Autowired DeputatExeContainer executables) {
        this.config = config;
        this.executables = executables;
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
        try {
            BotApiMethodMessage message = UpdateProcessor.process(update, executables);
            if (message != null)
                execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (ExecutableNotFoundException e) {
            log.error("Command '" + e.getMessage() + "' not found");
        }
    }

}