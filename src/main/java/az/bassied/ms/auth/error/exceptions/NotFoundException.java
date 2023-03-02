package az.bassied.ms.auth.error.exceptions;

public class NotFoundException extends GeneralException {
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
