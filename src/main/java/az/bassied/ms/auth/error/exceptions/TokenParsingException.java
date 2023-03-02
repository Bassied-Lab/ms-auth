package az.bassied.ms.auth.error.exceptions;

import az.bassied.ms.auth.model.consts.Messages;

public class TokenParsingException extends GeneralException {
    public TokenParsingException() {
        super(Messages.TOKEN_PARSING_EXP, Messages.TOKEN_PARSING_EXP_MSG);
    }
}
