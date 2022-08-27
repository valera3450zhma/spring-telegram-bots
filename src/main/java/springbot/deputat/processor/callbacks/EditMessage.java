package springbot.deputat.processor.callbacks;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import springbot.deputat.model.User;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.Button;
import springbot.telegram.CallbackAnswer;
import springbot.telegram.PropertyParser;
import springbot.telegram.generators.CallbackGenerator;
import springbot.telegram.generators.KeyboardGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditMessage {

    public static List<PartialBotApiMethod<?>> deputatMenu(CallbackAnswer answer, UserRepository userRepo) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();

        EditMessageText editMessageText = new EditMessageText(PropertyParser.getProperty("deputat.message"));
        editMessageText.setChatId(answer.getMessage().getChatId());
        editMessageText.setMessageId(answer.getMessage().getMessageId());
        actions.add(editMessageText);

        List<Button> buttons = new ArrayList<>();
        setDeputatButtons(answer.getUserId(), buttons, userRepo);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(answer.getMessage().getChatId());
        editMessageReplyMarkup.setMessageId(answer.getMessage().getMessageId());
        editMessageReplyMarkup.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
        actions.add(editMessageReplyMarkup);

        return actions;
    }

    public static void setDeputatButtons(Long userId, List<Button> buttons, UserRepository userRepo) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            User user = optionalUser.get();
            if (user.hasDeputat()) {
                buttons.add(new Button(PropertyParser.getProperty("deputat.button.show"),
                        PropertyParser.getProperty("deputat.callback.show")));
                buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill"),
                        PropertyParser.getProperty("deputat.callback.kill")));
            }
        } else {
            buttons.add(new Button(PropertyParser.getProperty("deputat.button.catch"),
                    PropertyParser.getProperty("deputat.callback.catch")));
        }
        CallbackGenerator.setUserId(buttons, userId);
    }

}
