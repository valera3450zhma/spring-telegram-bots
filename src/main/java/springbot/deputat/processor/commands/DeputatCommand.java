package springbot.deputat.processor.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.processor.callbacks.EditMessage;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.Button;
import springbot.telegram.PropertyParser;
import springbot.telegram.generators.CallbackGenerator;
import springbot.telegram.generators.KeyboardGenerator;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class DeputatCommand extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public DeputatCommand(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(Command.DEPUTAT.getTrigger());
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        log.info("Entered DeputatCommand");
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();

        String messageText = Objects.requireNonNull(PropertyParser.getProperty("deputat.message"));
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        sendMessage.setChatId(chatId);

        List<Button> buttons = new ArrayList<>();
        EditMessage.setDeputatButtons(userId, buttons, userRepo);
        CallbackGenerator.setUserId(buttons, userId);

        sendMessage.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        actions.add(sendMessage);
        log.info("Executed DeputatCommand");
        return actions;
    }

}
