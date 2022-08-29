package springbot.deputat.processor.callbacks.kill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.processor.callbacks.MenuGenerator;
import springbot.deputat.repo.DeputatRepository;
import springbot.deputat.repo.StatsRepository;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KillDeputatTrueCallback extends DeputatExecutable {

    private final UserRepository userRepo;
    private final DeputatRepository deputatRepo;
    private final StatsRepository statsRepo;

    @Autowired
    public KillDeputatTrueCallback(UserRepository userRepository, DeputatRepository deputatRepo, StatsRepository statsRepo) {
        this.userRepo = userRepository;
        this.deputatRepo = deputatRepo;
        this.statsRepo = statsRepo;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.kill.yes"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }
        killDeputat(answer, actions);
        actions.addAll(MenuGenerator.deputatMenu(answer, userRepo));

        return actions;
    }


    private void killDeputat(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        try {
            User user = userRepo.findUserWithDeputat(answer.getUserId());
            Deputat deputat = user.getDeputat();
            answer.getAnswerCallbackQuery().setText(deputat
                    .personalize(PropertyParser
                            .getRandom("entity.deputat.kill_message")));
            user.setDeputat(null);
            user.getStats().incrementKilledDeputats();
            statsRepo.save(user.getStats());
            userRepo.save(user);
            deputatRepo.delete(deputat);
        } catch (EntityNotFoundException e) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
        }
        actions.add(answer.getAnswerCallbackQuery());
    }

}
