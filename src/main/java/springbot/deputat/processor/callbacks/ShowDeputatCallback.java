package springbot.deputat.processor.callbacks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShowDeputatCallback extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public ShowDeputatCallback(UserRepository userRepository) {
        this.userRepo = userRepository;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("deputat.callback.show"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            return actions;
        }
        showDeputat(answer, actions);
        return actions;
    }

    private void showDeputat(CallbackAnswer answer, List<PartialBotApiMethod<?>> actions) {
        try {
            Deputat deputat = userRepo.findUserWithDeputat(answer.getUserId()).getDeputat();
            actions.add(prepareDeputatMessage(deputat, answer));
        } catch (EntityNotFoundException e) {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
            actions.add(answer.getAnswerCallbackQuery());
        }
    }

    private PartialBotApiMethod<?> prepareDeputatMessage(Deputat deputat, CallbackAnswer answer) {
        String messageText = PropertyParser.getProperty("deputat.reply.show");

        messageText = messageText.replaceFirst("\\?", deputat.getName());
        messageText = messageText.replaceFirst("\\?", String.valueOf(deputat.getMoney()));
        messageText = messageText.replaceFirst("\\?", String.valueOf(deputat.getRating()));
        messageText = messageText.replaceFirst("\\?", String.valueOf(deputat.getLevel()));
        messageText = messageText.replaceFirst("\\?", PropertyParser.getProperty("entity.deputat.level_caption." + deputat.getLevel()));

        SendPhoto sendPhoto = new SendPhoto(answer.getMessage().getChatId().toString(),
                new InputFile(deputat.getPhoto()));
        sendPhoto.setCaption(messageText);
        sendPhoto.setReplyToMessageId(answer.getMessage().getMessageId());
        return sendPhoto;
    }

}
