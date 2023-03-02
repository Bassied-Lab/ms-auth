package az.bassied.ms.auth.dao.repos;

import az.bassied.ms.auth.dao.entities.VerificationEntity;
import org.springframework.data.repository.CrudRepository;

public interface VerificationRepository extends CrudRepository<VerificationEntity, String> {

}
