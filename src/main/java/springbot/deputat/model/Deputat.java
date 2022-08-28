package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import springbot.telegram.PropertyParser;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Deputat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int level;
    private int money;
    private int rating;
    private String photo;
    private String name;
    private LocalDate lastWorked;

    public String personalize(String message) {
        return message.replaceFirst("\\?", name);
    }

    public void work(SendPhoto sendPhoto) {
        if (lastWorked != null && lastWorked.getDayOfYear() == LocalDate.now().getDayOfYear()) {
            sendPhoto.setPhoto(new InputFile(PropertyParser.getRandom("entity.deputat.worked_photo")));
            sendPhoto.setCaption(personalize(PropertyParser.getProperty("entity.deputat.work.failure")));
        } else {
            String[] earn = PropertyParser.getProperty("entity.deputat.work.earn."+level).split(" ");
            int minEarn = Integer.parseInt(earn[0]);
            int maxEarn = Integer.parseInt(earn[1]);
            int earned = ThreadLocalRandom.current().nextInt(minEarn, maxEarn + 1);
            sendPhoto.setPhoto(new InputFile(PropertyParser.getProperty("entity.deputat.work_photo." + level)));
            sendPhoto.setCaption(String.format("%s\n%s",
                    personalize(PropertyParser.getProperty("entity.deputat.work.success." + level)),
                    PropertyParser.getProperty("entity.deputat.work.success.earn")
                            .replaceFirst("\\?", String.valueOf(earned))));
            lastWorked = LocalDate.now();
            money += earned;
        }
    }
}
