package az.bassied.ms.auth.error.handler;

import az.bassied.ms.auth.error.ErrorResponse;
import az.bassied.ms.auth.error.exceptions.AuthException;
import az.bassied.ms.auth.error.exceptions.ClientException;
import az.bassied.ms.auth.error.exceptions.ForbiddenException;
import az.bassied.ms.auth.error.exceptions.GeneralException;
import az.bassied.ms.auth.error.exceptions.NotFoundException;
import az.bassied.ms.auth.error.exceptions.ValidationException;
import az.bassied.ms.auth.model.consts.Messages;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        logger.error("Action.handleMissingServletRequestParameter Given param is invalid: {}", ex.getParameterName());
        return new ResponseEntity<>(Map.of(ex.getParameterName(), ex.getParameterType()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Action.handleMethodArgumentNotValid Given params are invalid: {}", String.join(", ", errors.keySet()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException ex) {
        logger.error("Action.handleValidationException.error validate exception: {}", ex.toString());
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ForbiddenException.class})
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        logger.error("Action.handleForbiddenException.error forbidden exception: {}", ex.toString());
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        logger.error("Action.handleNotFoundException.error not found exception: {}", ex.toString());
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler({FeignException.class, ClientException.class})
    public ErrorResponse handleClientExceptions(RuntimeException ex) {
        logger.error("Action.handleClientExceptions.error server exception: {}", ex.toString());
        return new ErrorResponse(HttpStatus.BAD_GATEWAY.name(), Messages.SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler({GeneralException.class})
    public ErrorResponse handleGeneralExceptions(GeneralException ex) {
        logger.error("Action.handleGeneralExceptions.error general exception: {}", ex.toString());
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public ErrorResponse handleUnexpectedError(RuntimeException ex) {
        logger.error("Action.handleValidationException.error validate exception: {}", ex.toString());
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthException.class)
    public ErrorResponse handleAuthException(AuthException ex) {
        logger.error("Action.handleAuthException.error auth exception: {}", ex.toString());
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    //todo add handlers for new exceptions

}
