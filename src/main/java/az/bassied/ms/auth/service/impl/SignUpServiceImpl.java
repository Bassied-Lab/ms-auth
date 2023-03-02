package az.bassied.ms.auth.service.impl;

import az.bassied.ms.auth.client.UserClient;
import az.bassied.ms.auth.dao.entities.VerificationEntity;
import az.bassied.ms.auth.dao.repos.VerificationRepository;
import az.bassied.ms.auth.error.exceptions.NotFoundException;
import az.bassied.ms.auth.model.common.SignUpDTO;
import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.consts.Headers;
import az.bassied.ms.auth.model.consts.Messages;
import az.bassied.ms.auth.service.SignUpService;
import az.bassied.ms.auth.util.VerificationUtil;
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

    private final VerificationRepository verificationRepo;
    private final VerificationUtil util;

    @Override
    public void signUp(SignUpDTO request) {
        MDC.put(Headers.USER_EMAIL, request.email());
        logger.info("Action.signUp.start");
        UserDTO user = userClient.create(request);
        String token = util.generateToken();
        //todo send verification code by email
        logger.debug("Action.debug verification token for email {} is {}", user.email(), token);
        verificationRepo.save(VerificationEntity.builder().email(user.email()).token(token).build());
        logger.info("ActionLog.signUp.end");
    }

    @Override
    public void confirm(String token) {
        logger.debug("Action.confirm.start for {}", token);

        var verification = verificationRepo
                .findById(token)
                .orElseThrow(() -> new NotFoundException(Messages.TOKEN_NOT_FOUND, Messages.TOKEN_NOT_FOUND_MSG));

        logger.debug("Action.confirm for {}", verification.getEmail());

        UserDTO user = userClient.activateUserByEmail(verification.getEmail());
        //todo send mail
        //notificationService.sendWelcomeNotification(user);

        verificationRepo.delete(verification);
        logger.debug("Action.confirm.end for {}", token);
    }
}
