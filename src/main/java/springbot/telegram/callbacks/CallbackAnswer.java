package springbot.telegram.callbacks;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import springbot.telegram.PropertyParser;

@Data
public class CallbackAnswer {

    private AnswerCallbackQuery answerCallbackQuery;
    private Message message;
    private Long userId;
    private User user;
    private Long messageOwnerId;

    public CallbackAnswer(Update update) {
        this.answerCallbackQuery = new AnswerCallbackQuery(update.getCallbackQuery().getId());
        this.answerCallbackQuery.setShowAlert(true);
        this.message = update.getCallbackQuery().getMessage();
        this.userId = update.getCallbackQuery().getFrom().getId();
        this.user = update.getCallbackQuery().getFrom();
        this.messageOwnerId = Long.parseLong(update.getCallbackQuery().getData().split(" ")[1]);
    }

    public boolean hasNoAccess() {
        if (userId.equals(messageOwnerId)) {
            return false;
        } else {
            answerCallbackQuery.setText(PropertyParser.getProperty("error.access.callback"));
            return true;
        }
    }

}
