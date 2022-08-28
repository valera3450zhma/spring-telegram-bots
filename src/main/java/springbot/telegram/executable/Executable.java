package springbot.telegram.executable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import springbot.deputat.config.BotConfig;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    protected abstract List<PartialBotApiMethod<?>> run(Update update);

    public List<PartialBotApiMethod<?>> execute(Update update) {
        Long startTime = System.currentTimeMillis();
        log.info("Entered {}", this.getClass().getName());
        List<PartialBotApiMethod<?>> actions = run(update);
        Long finishTime = System.currentTimeMillis();
        log.info("Finished {}, elapsed {}", this.getClass().getName(), finishTime-startTime);
        return actions;
    }
}
