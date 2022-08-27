package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.Button;
import springbot.telegram.CallbackAnswer;
import springbot.telegram.PropertyParser;
import springbot.telegram.generators.CallbackGenerator;
import springbot.telegram.generators.KeyboardGenerator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KillDeputatCallback extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public KillDeputatCallback(UserRepository userRepository) {
        this.userRepo = userRepository;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.kill"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        log.info("Entered ShowDeputatCallback");
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }
        sendCheck(answer, actions);

        log.info("Finished ShowDeputatCallback");
        return actions;
    }

    private void sendCheck(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        Optional<User> optionalUser = userRepo.findById(answer.getUserId());
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            EditMessageText editMessageText = new EditMessageText(PropertyParser.getProperty("deputat.query.kill.check"));
            editMessageText.setChatId(answer.getMessage().getChatId());
            editMessageText.setMessageId(answer.getMessage().getMessageId());
            actions.add(editMessageText);

            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(answer.getMessage().getChatId());
            editMessageReplyMarkup.setMessageId(answer.getMessage().getMessageId());

            List<Button> buttons = new ArrayList<>();
            buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill.yes"),
                    PropertyParser.getProperty("deputat.callback.kill.yes")));
            buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill.no"),
                    PropertyParser.getProperty("deputat.callback.kill.no")));

            CallbackGenerator.setUserId(buttons, answer.getUserId());

            editMessageReplyMarkup.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
            actions.add(editMessageReplyMarkup);
        } else {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
            actions.add(answer.getAnswerCallbackQuery());
        }
    }


}
