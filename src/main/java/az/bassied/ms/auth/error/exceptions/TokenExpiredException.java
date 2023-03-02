package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class TokenExpiredException extends GeneralException {
    public TokenExpiredException() {
        super(Messages.ACCESS_TOKEN_EXP, Messages.ACCESS_TOKEN_EXP_MSG);
    }
}
