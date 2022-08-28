package springbot.deputat.processor.callbacks;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import springbot.deputat.model.User;
import springbot.deputat.repo.UserRepository;
import springbot.telegram.callbacks.Button;
import springbot.telegram.callbacks.CallbackAnswer;
import springbot.telegram.PropertyParser;
import springbot.telegram.callbacks.KeyboardGenerator;

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
                buttons.add(new Button(PropertyParser.getProperty("deputat.button.work"),
                        PropertyParser.getProperty("deputat.callback.work")));
                buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill"),
                        PropertyParser.getProperty("deputat.callback.kill")));
            }
        } else {
            buttons.add(new Button(PropertyParser.getProperty("deputat.button.catch"),
                    PropertyParser.getProperty("deputat.callback.catch")));
        }
        setUserId(buttons, userId);
    }

    public static List<PartialBotApiMethod<?>> killDeputatMenu(CallbackAnswer answer, UserRepository userRepo) {
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();
        Optional<User> optionalUser = userRepo.findById(answer.getUserId());
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            EditMessageText editMessageText = new EditMessageText(PropertyParser.getProperty("deputat.query.kill.check"));
            editMessageText.setChatId(answer.getMessage().getChatId());
            editMessageText.setMessageId(answer.getMessage().getMessageId());
            actions.add(editMessageText);

            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(answer.getMessage().getChatId());
            editMessageReplyMarkup.setMessageId(answer.getMessage().getMessageId());
            List<Button> buttons = new ArrayList<>();
            setKillDeputatButtons(buttons, answer.getUserId());
            editMessageReplyMarkup.setReplyMarkup(KeyboardGenerator.generateInline(buttons));
            actions.add(editMessageReplyMarkup);
        } else {
            answer.getAnswerCallbackQuery().setText(PropertyParser.getProperty("deputat.query.exists.not"));
            actions.add(answer.getAnswerCallbackQuery());
        }
        return actions;
    }

    public static void setKillDeputatButtons(List<Button> buttons, Long userId) {
        buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill.yes"),
                PropertyParser.getProperty("deputat.callback.kill.yes")));
        buttons.add(new Button(PropertyParser.getProperty("deputat.button.kill.no"),
                PropertyParser.getProperty("deputat.callback.kill.no")));
        setUserId(buttons, userId);
    }


    private static void setUserId(List<Button> buttons, Long userId) {
        for (Button button : buttons) {
            button.setCallbackData(button.getCallbackData().replace("?", userId.toString()));
        }
    }

    public static void setAdminButtons(Long userId, List<Button> buttons) {
        buttons.add(new Button(PropertyParser.getProperty("admin.button.reset_work"),
                PropertyParser.getProperty("admin.callback.reset_work")));
        setUserId(buttons, userId);
    }
}
