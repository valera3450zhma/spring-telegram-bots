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
    private boolean isAdmin = false;

    @OneToOne
    private Deputat deputat;

    public boolean hasDeputat() {
        return deputat != null;
    }

}
