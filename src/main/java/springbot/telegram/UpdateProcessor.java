package springbot.telegram;

import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class UpdateProcessor {

    public static boolean isCommand(Update update) {
        return update.getMessage().isCommand();
    }

    public static boolean isCallback(Update update) {
        return update.getCallbackQuery() != null;
    }

    public static BotApiMethodMessage process(Update update, ExecutablesContainer container) throws ExecutableNotFoundException {
        String updateCommand = getCommand(update);
        if (updateCommand.equals("")) {
            return null;
        }
        Executable executable = container.getExecutable(updateCommand);
        return executable.run(update);
    }

    private static String getCommand(Update update) {
        String updateCommand = "";
        if (isCommand(update)) {
            updateCommand = update.getMessage().getText();
        } else if (isCallback(update)) {
            updateCommand = update.getCallbackQuery().getData();
        }
        return updateCommand;
    }

}
