package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class TokenGenerationException extends GeneralException {
    public TokenGenerationException() {
        super(Messages.TOKEN_GEN_EXP, Messages.TOKEN_GEN_EXP_MSG);
    }
}