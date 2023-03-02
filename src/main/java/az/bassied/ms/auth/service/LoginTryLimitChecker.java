package az.bassied.ms.auth.service;

public interface LoginTryLimitChecker {

    void addTryOccurrence(String email);

    void lockAccountTemporarily(String email);

    void checkAccountLockStatus(String email);
}
