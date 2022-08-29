package springbot.deputat.repo;

import org.springframework.data.repository.CrudRepository;
import springbot.deputat.model.Stats;
import springbot.deputat.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface StatsRepository extends CrudRepository<Stats, Long> {

}
