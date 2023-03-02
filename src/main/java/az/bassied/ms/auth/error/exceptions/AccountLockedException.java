package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;
import lombok.Getter;

@Getter
public class AccountLockedException extends GeneralException {

    private final Long lockReleaseInMinutes;

    public AccountLockedException(Long lockReleaseInMinutes) {
        super(Messages.ACCOUNT_LOCKED_EXP, Messages.ACCOUNT_LOCKED_EXP_MSG);
        this.lockReleaseInMinutes = lockReleaseInMinutes;
    }
}
