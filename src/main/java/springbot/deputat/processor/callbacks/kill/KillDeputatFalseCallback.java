package springbot.deputat.processor.callbacks.kill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.processor.callbacks.MenuGenerator;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KillDeputatFalseCallback extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public KillDeputatFalseCallback(UserRepository userRepository) {
        this.userRepo = userRepository;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.kill.no"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }

        dontKillDeputat(answer, actions);
        actions.addAll(MenuGenerator.deputatMenu(answer, userRepo));

        return actions;
    }

    private void dontKillDeputat(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        try {
            Deputat deputat = userRepo.findUserWithDeputat(answer.getUserId()).getDeputat();
            answer.getAnswerCallbackQuery().setText(deputat.personalize(PropertyParser
                    .getProperty("deputat.query.kill.no")));
        } catch (EntityNotFoundException e) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("error.unknown"));
        }
        actions.add(answer.getAnswerCallbackQuery());
    }

}
