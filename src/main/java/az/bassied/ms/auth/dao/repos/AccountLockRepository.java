package az.bassied.ms.auth.dao.repos;

import az.bassied.ms.auth.dao.entities.AccountLockEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountLockRepository extends CrudRepository<AccountLockEntity, String> {

}
