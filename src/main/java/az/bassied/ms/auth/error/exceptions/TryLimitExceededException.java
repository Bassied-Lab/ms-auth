package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class TryLimitExceededException extends GeneralException {
    public TryLimitExceededException() {
        super(Messages.TRY_LIMIT_EXCEEDED_EXP, Messages.TRY_LIMIT_EXCEEDED_EXP_MSG);
    }
}
