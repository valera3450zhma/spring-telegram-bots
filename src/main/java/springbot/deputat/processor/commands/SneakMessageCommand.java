package springbot.deputat.processor.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.processor.DeputatExecutable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SneakMessageCommand extends DeputatExecutable {

    @PostConstruct
    public void setTriggers() {
        procTriggers("/sneak");
    }

    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        log.info("Entered SneakMessageCommand");
        List<PartialBotApiMethod<?>> actions = new ArrayList<>();

        String[] messageSplit = update.getMessage().getText().split(" ");
        String chatToSend = messageSplit[1];
        String textToSend = messageSplit[2];
        actions.add(new SendMessage(chatToSend, textToSend));

        log.info("Executed SneakMessageCommand");
        return actions;
    }


}
