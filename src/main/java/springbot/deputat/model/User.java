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
public class User {

    @Id
    private Long id;
    private String firstName;
    private String username;
    private boolean admin = false;

    @OneToOne
    private Stats stats;
    @OneToOne
    private Deputat deputat;

    public User(CallbackAnswer answer, boolean admin, Deputat deputat) {
        id = answer.getUserId();
        firstName = answer.getUser().getFirstName();
        username = answer.getUser().getUserName();
        this.admin = admin;
        this.deputat = deputat;
    }

    public static User update(User user, CallbackAnswer answer) {
        user.id = answer.getUserId();
        user.firstName = answer.getUser().getFirstName();
        user.username = answer.getUser().getUserName();
        return user;
    }

    public boolean hasDeputat() {
        return deputat != null;
    }

    public Stats getStats() {
        if (stats == null) {
            stats = new Stats();
        }
        return stats;
    }
}
