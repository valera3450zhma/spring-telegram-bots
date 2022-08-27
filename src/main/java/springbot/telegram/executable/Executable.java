package springbot.telegram.executable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.config.BotConfig;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@PropertySource("resources.properties")
public abstract class Executable {

    @Autowired
    protected Environment env;

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

    public abstract List<BotApiMethod<?>> run(Update update);
}
