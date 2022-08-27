package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        log.info("Entered ShowDeputatCallback");
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }

        dontKillDeputat(answer, actions);
        actions.addAll(EditMessage.deputatMenu(answer, userRepo));

        log.info("Finished ShowDeputatCallback");
        return actions;
    }

    private void dontKillDeputat(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        Optional<User> optionalUser = userRepo.findById(answer.getUserId());
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.kill.no")
                    .replaceFirst("\\?", optionalUser.get().getDeputat().getName()));
        } else {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("error.unknown"));
        }
        actions.add(answer.getAnswerCallbackQuery());
    }

}
