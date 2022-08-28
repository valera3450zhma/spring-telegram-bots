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
        log.info("Entered " + this.getClass().getName());
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }
        actions.addAll(EditMessage.killDeputatMenu(answer, userRepo));

        log.info("Finished " + this.getClass().getName());
        return actions;
    }

}
