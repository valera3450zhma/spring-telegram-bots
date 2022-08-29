package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private int killedDeputats = 0;

    @OneToOne
    private Deputat deputat;

    public User(CallbackAnswer answer, boolean admin, int killedDeputats, Deputat deputat) {
        id = answer.getUserId();
        firstName = answer.getUser().getFirstName();
        username = answer.getUser().getUserName();
        this.admin = admin;
        this.killedDeputats = killedDeputats;
        this.deputat = deputat;
    }

    public boolean hasDeputat() {
        return deputat != null;
    }

    public void incrementKilledDeputats() {
        killedDeputats++;
    }

}
