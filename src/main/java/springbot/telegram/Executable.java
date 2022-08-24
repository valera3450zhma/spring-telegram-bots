package springbot.telegram;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Executable {
    BotApiMethodMessage run(Update update);
}
