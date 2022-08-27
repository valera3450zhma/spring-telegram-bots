package springbot.deputat.repo;

import org.springframework.data.repository.CrudRepository;
import springbot.deputat.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
