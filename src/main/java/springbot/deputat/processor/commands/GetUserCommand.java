package springbot.deputat.processor.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.processor.DeputatExecutable;
import springbot.deputat.repo.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GetUserCommand extends DeputatExecutable {

    private final UserRepository userRepo;

    @Autowired
    public GetUserCommand(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void setTriggers() {
        procTriggers(Command.GET_USER.getTrigger());
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        Long userId = update.getMessage().getFrom().getId();
        if (userRepo.findUserByIdAndAdminTrue(userId) == null) {
            return actions;
        }
        getUser(update, actions);
        return actions;
    }

    private void getUser(Update update, List<PartialBotApiMethod<?>> actions) {
        String chatId = update.getMessage().getChatId().toString();
        String userIdToGet = update.getMessage().getText().split(" ")[1];
        String messageText = String.format("[user](tg://user?id=%s)", userIdToGet);
        SendMessage sendMessage = new SendMessage(chatId, messageText);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2);
        sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        actions.add(sendMessage);
    }

}
