package springbot.deputat.processor.callbacks.deputat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.Stats;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.processor.callbacks.MenuGenerator;
import springbot.deputat.repo.DeputatRepository;
import springbot.deputat.repo.StatsRepository;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class CatchDeputatCallback extends DeputatExecutable {

    private final DeputatRepository deputatRepo;
    private final UserRepository userRepo;
    private final StatsRepository statsRepo;

    @Autowired
    public CatchDeputatCallback(UserRepository userRepository, DeputatRepository deputatRepo, StatsRepository statsRepo) {
        this.deputatRepo = deputatRepo;
        this.userRepo = userRepository;
        this.statsRepo = statsRepo;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.catch"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        actions.add(answer.getAnswerCallbackQuery());
        if (answer.hasNoAccess())
            return actions;

        catchDeputat(answer);
        actions.addAll(MenuGenerator.deputatMenu(answer, userRepo));

        return actions;
    }

    private void catchDeputat(CallbackAnswer answer) {
        Long userId = answer.getUserId();
        AnswerCallbackQuery query = answer.getAnswerCallbackQuery();
        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            query.setText(PropertyParser.getProperty("deputat.query.exists"));
        } else {
            query.setText(PropertyParser.getProperty("deputat.query.catch"));
            Deputat deputat = randomDeputat();
            deputatRepo.save(deputat);
            User user = setUser(optionalUser, answer, deputat);
            userRepo.save(user);
        }
    }

    private User setUser(Optional<User> optionalUser, CallbackAnswer answer, Deputat deputat) {
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (user.getStats() == null) {
                setStats(user);
            }
            user.setDeputat(deputat);
        } else {
            user = new User(answer, false, deputat);
            setStats(user);
        }
        return user;
    }

    private void setStats(User user) {
        Stats stats = new Stats();
        statsRepo.save(stats);
        user.setStats(stats);
    }

    private Deputat randomDeputat() {
        Deputat deputat = new Deputat();

        String[] moneyBounds = PropertyParser.getProperty("entity.deputat.money").split(" ");
        int minMoney = Integer.parseInt(moneyBounds[0]);
        int maxMoney = Integer.parseInt(moneyBounds[1]);
        String randomName = PropertyParser.getRandom("entity.deputat.name");
        String randomPhoto = PropertyParser.getRandom("entity.deputat.photo.level.1");


        deputat.setLevel(1);
        deputat.setMoney(ThreadLocalRandom.current().nextInt(minMoney, maxMoney + 1));
        deputat.setRating(0);
        deputat.setName(randomName);
        deputat.setPhoto(randomPhoto);

        return deputat;
    }
}
