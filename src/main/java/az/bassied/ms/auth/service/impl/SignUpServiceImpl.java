package az.bassied.ms.auth.service.impl;

import az.bassied.ms.auth.client.UserClient;
import az.bassied.ms.auth.model.common.SignUpDTO;
import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.consts.Headers;

import az.bassied.ms.auth.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private static final Logger logger = LoggerFactory.getLogger(SignUpServiceImpl.class);

    private final UserClient userClient;

    @Override
    public void signUp(SignUpDTO request) {
        MDC.put(Headers.USER_EMAIL, request.email());
        logger.info("Action.signUp.start");

        UserDTO user = userClient.create(request);
        logger.debug("Action.signUp.debug created user id: {} email: {}", user.id(), user.email());
        //todo
        //sendVerificationEmail(user);
        logger.info("ActionLog.signUp.end");
    }

    @Override
    public void confirm(String token) {

    }
}
