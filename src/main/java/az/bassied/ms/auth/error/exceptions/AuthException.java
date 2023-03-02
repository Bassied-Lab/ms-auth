package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class AuthException extends GeneralException {
    public AuthException() {
        super(Messages.AUTH_EXP, Messages.AUTH_EXP_MSG);
    }
}