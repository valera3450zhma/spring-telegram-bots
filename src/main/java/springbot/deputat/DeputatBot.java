package springbot.deputat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import springbot.deputat.config.BotConfig;
import springbot.deputat.processor.DeputatExeContainer;
import springbot.deputat.processor.commands.Command;
import springbot.telegram.exceptions.ExecutableNotFoundException;
import springbot.telegram.UpdateProcessor;
import springbot.telegram.exceptions.NotAnExecutableException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeputatBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final DeputatExeContainer executables;

    public DeputatBot(BotConfig config, @Autowired DeputatExeContainer executables) {
        log.info("Creating {} instance", this.getClass().getName());
        this.config = config;
        this.executables = executables;
        setCommands();
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received an update: {}", update);
        try {
            List<PartialBotApiMethod<?>> actions = UpdateProcessor.process(update, executables);
            for (PartialBotApiMethod<?> action : actions) {
                executeGeneric(action);
            }
        } catch (ExecutableNotFoundException e) {
            log.error("Command not found: {}", e.getMessage());
        } catch (NotAnExecutableException e) {
            log.error("Received update was not an executable");
        }
    }

    private void setCommands() {
        try {
            List<BotCommand> commands = new ArrayList<>();
            for (Command command : Command.values()) {
                commands.add(new BotCommand(command.getTrigger(), command.getDescription()));
            }
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeGeneric(PartialBotApiMethod<?> method) {
        for (Method m : this.getClass().getMethods()) {
            if (m.getName().equals("execute")) {
                if (m.getParameters()[0].getType().equals(method.getClass())) {
                    try {
                        m.invoke(this, method);
                        return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
        try {
            this.execute((BotApiMethod<? extends Serializable>) method);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}