package az.bassied.ms.auth.dao.repos;

import az.bassied.ms.auth.dao.entities.UserSessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserSessionRepository extends CrudRepository<UserSessionEntity, String> {

    void deleteByEmail(String email);
}
