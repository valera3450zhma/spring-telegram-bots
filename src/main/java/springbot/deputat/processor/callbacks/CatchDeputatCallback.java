package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.DeputatRepository;
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

    @Autowired
    public CatchDeputatCallback(UserRepository userRepository, DeputatRepository deputatRepo) {
        this.deputatRepo = deputatRepo;
        this.userRepo = userRepository;
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
        actions.addAll(EditMessage.deputatMenu(answer, userRepo));

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
            User user = optionalUser.orElseGet(() -> new User(userId, false, 0, deputat));
            user.setDeputat(deputat);
            userRepo.save(user);
        }
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
