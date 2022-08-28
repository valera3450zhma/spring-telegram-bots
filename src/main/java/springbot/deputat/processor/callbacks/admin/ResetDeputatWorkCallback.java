package springbot.deputat.processor.callbacks.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.DeputatRepository;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ResetDeputatWorkCallback extends DeputatExecutable {

    private final DeputatRepository deputatRepo;
    private final UserRepository userRepo;

    @Autowired
    public ResetDeputatWorkCallback(UserRepository userRepository, DeputatRepository deputatRepo) {
        this.deputatRepo = deputatRepo;
        this.userRepo = userRepository;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("admin.callback.reset_work"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            actions.add(answer.getAnswerCallbackQuery());
            return actions;
        }

        try {
            Deputat deputat = userRepo.findUserWithDeputat(answer.getUserId()).getDeputat();
            deputat.setLastWorked(null);
            deputatRepo.save(deputat);
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("admin.query.reset_work"));
        } catch (EntityNotFoundException e) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
        }
        actions.add(answer.getAnswerCallbackQuery());
        return actions;
    }

}
