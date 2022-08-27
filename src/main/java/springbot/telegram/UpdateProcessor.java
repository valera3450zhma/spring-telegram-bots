package springbot.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.telegram.exceptions.NotAnExecutableException;
import springbot.telegram.executable.Executable;
import springbot.telegram.exceptions.ExecutableNotFoundException;
import springbot.telegram.executable.ExecutablesContainer;

import java.util.List;

@Service
public class UpdateProcessor {

    public static List<BotApiMethod<?>> process(Update update, ExecutablesContainer container) throws NotAnExecutableException, ExecutableNotFoundException {
        String trigger = getTrigger(update);
        Executable executable = container.getExecutable(trigger);
        return executable.run(update);
    }

    private static String getTrigger(Update update) throws NotAnExecutableException {
        if (isCommand(update)) {
            return update.getMessage().getText().split(" ")[0];
        } else if (isCallback(update)) {
            return update.getCallbackQuery().getData().split(" ")[0];
        } else {
            throw new NotAnExecutableException();
        }
    }

    private static boolean isCommand(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().charAt(0) == '/';
    }

    private static boolean isCallback(Update update) {
        return update.hasCallbackQuery();
    }

}
