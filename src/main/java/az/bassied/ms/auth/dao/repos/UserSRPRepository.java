package az.bassied.ms.auth.dao.repos;

import az.bassied.ms.auth.dao.entities.UserSRPEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserSRPRepository extends CrudRepository<UserSRPEntity, String> {

    void deleteByEmail(String email);

}
