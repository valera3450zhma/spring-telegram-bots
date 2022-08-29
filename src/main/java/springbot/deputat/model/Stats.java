package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springbot.telegram.PropertyParser;
import springbot.telegram.callbacks.CallbackAnswer;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int killedDeputats = 0;
    private int earned = 0;
    private int spent = 0;

    public void incrementKilledDeputats() {
        killedDeputats++;
    }

    public void incrementEarned(int earned) {
        this.earned += earned;
    }

    public String parseStats() {
        String template = PropertyParser.getProperty("user.query.stats");
        template = template.replaceFirst("\\?", String.valueOf(killedDeputats));
        template = template.replaceFirst("\\?", String.valueOf(earned));
        template = template.replaceFirst("\\?", String.valueOf(spent));
        return template;
    }
}
