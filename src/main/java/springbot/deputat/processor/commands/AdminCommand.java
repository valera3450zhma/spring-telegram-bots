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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdminCommand extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public AdminCommand(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(Command.ADMIN.getTrigger());
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        Long userId = update.getMessage().getFrom().getId();
        if (userRepo.findUserByIdAndAdminTrue(userId) == null) {
            return actions;
        }
        prepareAdminMenu(update, actions);
        return actions;
    }

    private void prepareAdminMenu(Update update, List<PartialBotApiMethod<?>> actions) {
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage(chatId, PropertyParser.getProperty("admin.message"));
        sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        List<Button> buttons = new ArrayList<>();
        MenuGenerator.setAdminButtons(userId, buttons);
        sendMessage.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        actions.add(sendMessage);
    }

}
