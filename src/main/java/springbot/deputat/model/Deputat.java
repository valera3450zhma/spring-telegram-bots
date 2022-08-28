package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    private Date lastWorked;

    public String personalize(String message) {
        return message.replaceFirst("\\?", name);
    }

}
