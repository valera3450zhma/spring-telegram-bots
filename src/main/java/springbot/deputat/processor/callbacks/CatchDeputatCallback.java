package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.DeputatRepository;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.Button;
import springbot.telegram.PropertyParser;
import springbot.telegram.generators.CallbackGenerator;
import springbot.telegram.generators.KeyboardGenerator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@PropertySource("resources.properties")
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
        procTriggers(env.getProperty("deputat.callback.catch"));
    }

    @Override
    public List<BotApiMethod<?>> run(Update update) {
        log.info("Entered CatchDeputatCallback");
        List<BotApiMethod<?>> actions = new ArrayList<>();

        Long userId = update.getCallbackQuery().getFrom().getId();
        String callbackOwnerId = update.getCallbackQuery().getData().split(" ")[1];
        AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());
        callbackQuery.setShowAlert(true);
        actions.add(callbackQuery);
        
        if (!userId.toString().equals(callbackOwnerId)) {
            callbackQuery.setText(env.getProperty("error.access.callback"));
            return actions;
        }

        catchDeputat(userId, callbackQuery);
        EditMessageReplyMarkup editMessageReplyMarkup = setButtons(update, userId);
        actions.add(editMessageReplyMarkup);

        log.info("Executed CatchDeputatCallback");
        return actions;
    }

    private EditMessageReplyMarkup setButtons(Update update, Long userId) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

        Button[] buttons = new Button[] {new Button()};
        buttons[0].setLabel(env.getProperty("deputat.button.show"));
        buttons[0].setCallbackData(env.getProperty("deputat.callback.show"));
        CallbackGenerator.setUserId(buttons, userId);

        editMessageReplyMarkup.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        return editMessageReplyMarkup;
    }

    private void catchDeputat(Long userId, AnswerCallbackQuery query) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            query.setText(env.getProperty("deputat.query.exists"));
        } else {
            query.setText(env.getProperty("deputat.query.catch"));
            Deputat deputat = randomDeputat();
            deputat = deputatRepo.save(deputat);
            User user = new User(userId, deputat);
            userRepo.save(user);
        }
    }

    private Deputat randomDeputat() {
        Deputat deputat = new Deputat();

        int minMoney = Integer.parseInt(Objects.requireNonNull(env.getProperty("entity.deputat.money.min")));
        int maxMoney = Integer.parseInt(Objects.requireNonNull(env.getProperty("entity.deputat.money.max")));
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
