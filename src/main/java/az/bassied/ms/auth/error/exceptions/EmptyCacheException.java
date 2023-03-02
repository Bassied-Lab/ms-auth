package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class EmptyCacheException extends GeneralException {

    public EmptyCacheException() {
        super(Messages.EMPTY_CACHE_EXP, Messages.EMPTY_CACHE_EXP_MSG);
    }
}
