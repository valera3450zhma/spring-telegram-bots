package springbot.deputat.repo;

import org.springframework.data.repository.CrudRepository;
import springbot.deputat.model.Deputat;
import springbot.deputat.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    default User findUserWithDeputat(Long userId) throws EntityNotFoundException {
        Optional<User> optionalUser = findById(userId);
        if (optionalUser.isPresent() && optionalUser.get().hasDeputat()) {
            return optionalUser.get();
        }
        throw new EntityNotFoundException();
    }

    User findUserByIdAndAdminTrue(Long userId);

}
