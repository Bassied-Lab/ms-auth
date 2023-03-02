package az.bassied.ms.auth.error.exceptions;

public class ForbiddenException extends GeneralException {
    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}
