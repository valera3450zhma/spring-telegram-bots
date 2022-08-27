package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.DeputatRepository;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KillDeputatTrueCallback extends DeputatExecutable {

    private final UserRepository userRepo;
    private final DeputatRepository deputatRepo;

    @Autowired
    public KillDeputatTrueCallback(UserRepository userRepository, DeputatRepository deputatRepo) {
        this.userRepo = userRepository;
        this.deputatRepo = deputatRepo;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.kill.yes"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        log.info("Entered ShowDeputatCallback");
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }
        killDeputat(answer, actions);
        actions.addAll(EditMessage.deputatMenu(answer, userRepo));

        log.info("Finished ShowDeputatCallback");
        return actions;
    }


    private void killDeputat(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        Optional<User> optionalUser = userRepo.findById(answer.getUserId());
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            User user = optionalUser.get();
            answer.getAnswerCallbackQuery().setText(PropertyParser.getRandom("entity.deputat.kill_message")
                    .replaceFirst("\\?", user.getDeputat().getName()));
            Deputat deputat = user.getDeputat();
            user.setDeputat(null);
            userRepo.save(user);
            deputatRepo.delete(deputat);
        } else {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
        }
        actions.add(answer.getAnswerCallbackQuery());
    }

}
