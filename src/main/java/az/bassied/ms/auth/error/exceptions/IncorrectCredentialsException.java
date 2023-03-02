package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class IncorrectCredentialsException extends GeneralException {

    public IncorrectCredentialsException() {
        super(Messages.INVALID_CREDENTIALS_EXP, Messages.INVALID_CREDENTIALS_EXP_MSG);
    }
}
