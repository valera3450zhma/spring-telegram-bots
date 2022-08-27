package springbot.deputat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    @OneToOne
    private Deputat deputat;

    public boolean hasDeputat() {
        return deputat != null;
    }

}
