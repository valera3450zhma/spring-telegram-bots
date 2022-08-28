package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long id;
    private boolean admin = false;
    private int killedDeputats = 0;

    @OneToOne
    private Deputat deputat;

    public boolean hasDeputat() {
        return deputat != null;
    }

    public void incrementKilledDeputats() {
        killedDeputats++;
    }

}
