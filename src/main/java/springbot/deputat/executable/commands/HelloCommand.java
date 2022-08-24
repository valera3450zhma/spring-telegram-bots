package springbot.deputat.executable.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.executable.DeputatExecutable;

@Slf4j
@Service("/hi")
public class HelloCommand implements DeputatExecutable {
    @Override
    public BotApiMethodMessage run(Update update) {
        log.info("Entered HelloCommand");
        String messageText = "Hi!";
        String chatId = update.getMessage().getChatId().toString();
        log.info("Executed HelloCommand");
        return new SendMessage(chatId, messageText);
    }
}
