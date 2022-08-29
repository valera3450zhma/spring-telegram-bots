package springbot.deputat.processor.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.processor.callbacks.MenuGenerator;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.Button;
import springbot.telegram.PropertyParser;
import springbot.telegram.callbacks.KeyboardGenerator;

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
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();

        String chatId = update.getMessage().getChatId().toString();
        Long userId = update.getMessage().getFrom().getId();

        SendMessage sendMessage = new SendMessage(chatId, PropertyParser.getProperty("deputat.message"));

        List<Button> buttons = new ArrayList<>();
        MenuGenerator.setDeputatButtons(userId, buttons, userRepo);

        sendMessage.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        actions.add(sendMessage);
        return actions;
    }

}
