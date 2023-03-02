package az.bassied.ms.auth.service.impl;

import az.bassied.ms.auth.dao.entities.AccountLockEntity;
import az.bassied.ms.auth.dao.entities.TryOccurrenceEntity;
import az.bassied.ms.auth.dao.repos.AccountLockRepository;
import az.bassied.ms.auth.dao.repos.TryOccurrenceRepository;
import az.bassied.ms.auth.error.exceptions.AccountLockedException;
import az.bassied.ms.auth.error.exceptions.TryLimitExceededException;
import az.bassied.ms.auth.service.LoginTryLimitChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoginTryLimitCheckerImpl implements LoginTryLimitChecker {

    private static final Logger logger = LoggerFactory.getLogger(LoginTryLimitCheckerImpl.class);

    private final TryOccurrenceRepository tryOccurrenceRepository;
    private final AccountLockRepository accountLockRepository;
    private final Long accountLockTTL;
    private final int maxTryCount;

    public LoginTryLimitCheckerImpl(TryOccurrenceRepository tryOccurrenceRepository,
                                    AccountLockRepository accountLockRepository,
                                    @Value("${bucket.account.lock.ttl}") Long accountLockTTL,
                                    @Value("${bucket.incorrectCredentialTry.maxTryCount}") int maxTryCount) {
        this.tryOccurrenceRepository = tryOccurrenceRepository;
        this.accountLockRepository = accountLockRepository;
        this.accountLockTTL = accountLockTTL;
        this.maxTryCount = maxTryCount;
    }

    @Override
    public void addTryOccurrence(String email) {
        logger.debug("Action.addTryOccurrence.start for email {}", email);

        var entity = tryOccurrenceRepository
                .findById(email)
                .orElse(tryOccurrenceRepository.save(TryOccurrenceEntity.builder().email(email).count(0).build()));
        entity.setCount(entity.getCount() + 1);

        checkTryLimitExceeded(email, entity.getCount());

        tryOccurrenceRepository.save(entity);

        logger.debug("Action.addTryOccurrence.end for email {}", email);

    }

    @Override
    public void lockAccountTemporarily(String email) {
        logger.debug("Account.lockAccountTemporary.start for email {}", email);

        var lockExpireTime = LocalDateTime.now().plusSeconds(accountLockTTL);

        accountLockRepository.save(AccountLockEntity.builder().email(email).lockExpireDate(lockExpireTime).build());

        logger.debug("Account.lockAccountTemporary.end for email {}", email);
    }

    @Override
    public void checkAccountLockStatus(String email) {

        Optional<AccountLockEntity> entity = accountLockRepository.findById(email);

        if (entity.isPresent()) {
            logger.warn("Action.checkAccountLockStatus account is locked for email {}", email);
            //todo handle exceptions
            long lockReleaseInMinutes = getMinutesToExpire(entity.get().getLockExpireDate());
            throw new AccountLockedException(lockReleaseInMinutes);
        }


    }

    public void checkTryLimitExceeded(String email, Integer count) {
        if (count > maxTryCount) {
            logger.debug("Action.checkTryLimitExceeded for email {}", email);
            throw new TryLimitExceededException();
        }
    }

    private long getMinutesToExpire(LocalDateTime lockExpireTime) {
        return Duration.between(LocalDateTime.now(), lockExpireTime).toMinutes();
    }
}
