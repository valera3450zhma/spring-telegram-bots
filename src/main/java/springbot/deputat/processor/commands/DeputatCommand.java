package springbot.deputat.processor.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.Button;
import springbot.telegram.generators.CallbackGenerator;
import springbot.telegram.generators.KeyboardGenerator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public List<BotApiMethod<?>> run(Update update) {
        log.info("Entered DeputatCommand");
        List<BotApiMethod<?>> actions = new ArrayList<>();

        String messageText = Objects.requireNonNull(env.getProperty("deputat.message"));
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        sendMessage.setChatId(chatId);

        Button[] buttons = new Button[] {new Button()};

        setButton(userId, buttons[0]);

        CallbackGenerator.setUserId(buttons, userId);
        sendMessage.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        actions.add(sendMessage);

        log.info("Executed DeputatCommand");
        return actions;
    }

    private void setButton(Long userId, Button button) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.hasDeputat()) {
                button.setLabel(env.getProperty("deputat.button.show"));
                button.setCallbackData(env.getProperty("deputat.callback.show"));
            }
            else {
                button.setLabel(env.getProperty("deputat.button.catch"));
                button.setCallbackData(env.getProperty("deputat.callback.catch"));
            }
        } else {
            button.setLabel(env.getProperty("deputat.button.catch"));
            button.setCallbackData(env.getProperty("deputat.callback.catch"));
        }
    }

}
