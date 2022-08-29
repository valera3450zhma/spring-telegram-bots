package springbot.deputat.processor.callbacks.deputat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
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
public class WorkDeputatCallback extends DeputatExecutable {

    private final DeputatRepository deputatRepo;
    private final UserRepository userRepo;
    private final StatsRepository statsRepo;

    @Autowired
    public WorkDeputatCallback(UserRepository userRepository, DeputatRepository deputatRepo, StatsRepository statsRepo) {
        this.deputatRepo = deputatRepo;
        this.userRepo = userRepository;
        this.statsRepo = statsRepo;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.work"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            actions.add(answer.getAnswerCallbackQuery());
            return actions;
        }
        work(answer, actions);

        return actions;
    }

    private void work(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        try {
            User user = userRepo.findUserWithDeputat(answer.getUserId());
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(answer.getMessage().getChatId());
            sendPhoto.setReplyToMessageId(answer.getMessage().getMessageId());
            Deputat deputat = user.getDeputat();
            int earned = deputat.work(sendPhoto);
            actions.add(sendPhoto);
            if (earned != 0) {
                deputatRepo.save(deputat);
                user.getStats().incrementEarned(earned);
                statsRepo.save(user.getStats());
            }
            userRepo.save(User.update(user, answer));
        } catch (EntityNotFoundException e) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
            actions.add(answer.getAnswerCallbackQuery());
        }
    }

}
