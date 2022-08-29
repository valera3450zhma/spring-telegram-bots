package springbot.deputat.processor.callbacks.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.PropertyParser;
import springbot.telegram.callbacks.CallbackAnswer;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StatsCallback extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public StatsCallback(UserRepository userRepository) {
        this.userRepo = userRepository;
    }


    @PostConstruct
    public void setTriggers() {
        procTriggers(PropertyParser.getProperty("user.callback.stats"));
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        CallbackAnswer answer = new CallbackAnswer(update);
        if (answer.hasNoAccess()) {
            actions.add(answer.getAnswerCallbackQuery());
            return actions;
        }
        getStats(actions, answer);

        return actions;
    }

    private void getStats(List<PartialBotApiMethod<?>> actions, CallbackAnswer answer) {
        Optional<User> optionalUser = userRepo.findById(answer.getUserId());
        if (optionalUser.isPresent()) {
            String messageText = optionalUser.get().getStats().parseStats();
            String chatId = answer.getMessage().getChatId().toString();
            SendMessage sendMessage = new SendMessage(chatId, messageText);
            sendMessage.setReplyToMessageId(answer.getMessage().getMessageId());
            actions.add(sendMessage);
        } else {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
            actions.add(answer.getAnswerCallbackQuery());
        }
    }


}
