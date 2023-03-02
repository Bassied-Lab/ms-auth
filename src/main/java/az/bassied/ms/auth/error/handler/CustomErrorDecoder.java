package az.bassied.ms.auth.error.handler;

import az.bassied.ms.auth.error.ErrorResponse;
import az.bassied.ms.auth.error.exceptions.*;
import az.bassied.ms.auth.model.consts.Messages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorDecoder.class);

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse errResp;
        try {
            logger.warn("Action.logRequestInfo client methodKey: {}", methodKey);
            var reader = response.body().asReader(StandardCharsets.UTF_8);
            var responseJson = CharStreams.toString(reader);
            logger.warn("Action.decode Response status: {} body: {}", response.status(), responseJson);
            errResp = objectMapper.readValue(responseJson, ErrorResponse.class);
        } catch (Exception ex) {
            logger.error("Action.decode Failed to serialize error response: {}", ex.getMessage());
            return new ClientException(response.reason());
        }
        final String errorMessage = errResp.message();
        return switch (response.status()) {
            case 400 -> new ValidationException(errResp.code(), errorMessage);
            case 403 -> new ForbiddenException(errResp.code(), errorMessage);
            case 404 -> {
                if (errorMessage.isEmpty())
                    yield new NotFoundException(Messages.PATH_NOT_FOUND, Messages.PATH_NOT_FOUND);
                else yield new NotFoundException(errResp.code(), errorMessage);

            }
            default -> new GeneralException(errResp.code(), errorMessage);
        };
    }

}
