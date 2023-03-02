package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class RefreshTokenException extends GeneralException {

    public RefreshTokenException() {
        super(Messages.REFRESH_TOKEN_EXP, Messages.REFRESH_TOKEN_EXP_MSG);
    }
}
