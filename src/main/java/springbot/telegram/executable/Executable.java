package springbot.telegram.executable;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.config.BotConfig;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public abstract class Executable {

    @Autowired
    private BotConfig botConfig;

    @PostConstruct
    public abstract void setTriggers();

    protected final List<String> triggers = new ArrayList<>();

    protected final void procTriggers(String... trigger) {
        for (String t : trigger) {
            triggers.add(t.split(" ")[0]);
            triggers.add(String.format("%s@%s", t, botConfig.getName()));
        }
    }

    public final List<String> getTriggers() {
        return triggers;
    }

    public abstract List<PartialBotApiMethod<?>> run(Update update);
}
