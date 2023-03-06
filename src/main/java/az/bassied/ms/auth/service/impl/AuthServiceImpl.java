package az.bassied.ms.auth.service.impl;

import az.bassied.ms.auth.client.UserClient;
import az.bassied.ms.auth.crypto.SRP6Helper;
import az.bassied.ms.auth.dao.entities.UserSRPEntity;
import az.bassied.ms.auth.dao.repos.UserSRPRepository;
import az.bassied.ms.auth.dao.repos.UserSessionRepository;
import az.bassied.ms.auth.error.exceptions.AccountLockedException;
import az.bassied.ms.auth.error.exceptions.AuthException;
import az.bassied.ms.auth.error.exceptions.EmptyCacheException;
import az.bassied.ms.auth.error.exceptions.IncorrectCredentialsException;
import az.bassied.ms.auth.error.exceptions.TryLimitExceededException;
import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.consts.Headers;
import az.bassied.ms.auth.model.enums.TokenIssuer;
import az.bassied.ms.auth.model.jwt.AuthTokensDTO;
import az.bassied.ms.auth.model.jwt.NewPasswordDTO;
import az.bassied.ms.auth.model.srp.SrpStep1Req;
import az.bassied.ms.auth.model.srp.SrpStep1Res;
import az.bassied.ms.auth.model.srp.SrpStep2Req;
import az.bassied.ms.auth.model.srp.SrpStep2Res;
import az.bassied.ms.auth.service.AuthService;
import az.bassied.ms.auth.service.LoginTryLimitChecker;
import az.bassied.ms.auth.service.TokenService;
import az.bassied.ms.auth.util.JwtUtil;
import com.nimbusds.srp6.SRP6ServerSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final SRP6Helper srp6Helper;
    private final UserClient userClient;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;
    private final LoginTryLimitChecker loginTryLimitChecker;
    private final UserSRPRepository userSRPRepository;
    private final UserSessionRepository userSessionRepository;


    @Override
    public SrpStep1Res srpAuthStep1(SrpStep1Req req) {
        MDC.put(Headers.USER_EMAIL, req.email());
        logger.info("Action.srpAuthStep.start");

        try {
            loginTryLimitChecker.checkAccountLockStatus(req.email());

            // Step 1: get user
            UserDTO user = userClient.getUserByEmail(req.email());

            MDC.put(Headers.USER_EMAIL, user.id().toString());
            logger.debug("Action.srpAuthStep.debug found user with email {}", user.email());

            SrpStep1Res res;
            if (!StringUtils.hasText(user.salt()) &&
                    !StringUtils.hasText(user.verifier())) {
                logger.debug("Action.srpAuthStep.debug user has no password in DB, using fake SRP1");
                res = doFakeSrp1(user);
            } else {
                logger.debug("Action.srpAuthStep.debug user has password in DB, using real SRP1");
                res = doRealSrp1(user);
            }

            logger.info("Action.srpAuthStep.success");
            return res;
        } catch (AccountLockedException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Action.srpAuthStep.fail ex: {}", ex.toString());
            return doFakeSrp1(req.email());
        }
    }

    @Override
    public Pair<SrpStep2Res, AuthTokensDTO> srpAuthStep2(SrpStep2Req req) {
        try {
            MDC.put(Headers.USER_EMAIL, req.email());
            logger.info("Action.srpAuthStep2.start");

            UserSRPEntity srpEntity = userSRPRepository.findById(req.email()).orElseThrow(EmptyCacheException::new);

            SRP6ServerSession srpSession = (SRP6ServerSession) srpEntity.getSrpSession();
            SrpStep2Res srpStep2Res = srp6Helper.doRealSrp2(req, srpSession, srpEntity.getUserId());

            AuthTokensDTO tokens = tokenService.generateTokens(srpEntity.getEmail(), TokenIssuer.BS);

            userSRPRepository.delete(srpEntity);

            logger.info("Action.srpAuthStep2.success");
            return Pair.of(srpStep2Res, tokens);
        } catch (Exception ex) {
            logger.error("ActionLog.srpAuthStep2.fail ex: {}", ex.toString());
            try {
                loginTryLimitChecker.addTryOccurrence(req.email());
            } catch (TryLimitExceededException tryLimitEx) {
                logger.warn("Account is going to be locked {}", req.email());
                loginTryLimitChecker.lockAccountTemporarily(req.email());
            }
            throw new IncorrectCredentialsException();
        }
    }


    @Override
    public void logout(String accessToken) {
        logger.debug("Action.logout.start");
        var claimsSet = jwtUtil.getClaimsFromAccessToken(accessToken);
        userSessionRepository.deleteByEmail(claimsSet.getEmail());
        logger.debug("Action.logout.end for email {} ", claimsSet.getEmail());
    }

    //todo add reset password
    @Override
    public AuthTokensDTO changePassword(String accessToken, NewPasswordDTO request) {
        try {
            logger.info("Action.changePasswordStep.start");
            UserDTO user = tokenService.validateToken(accessToken);

            userClient.changePassword(user.id(), request);

            AuthTokensDTO tokens = tokenService.generateTokens(user.email(), TokenIssuer.BS);
            userSRPRepository.deleteByEmail(user.email());
            logger.info("Action.changePasswordStep.success");
            return new AuthTokensDTO(tokens.accessToken(), tokens.refreshToken());
        } catch (Exception ex) {
            logger.error("Action.changePasswordStep.fail ex: {}", ex.toString());
            throw new AuthException();
        }
    }

    private SrpStep1Res doRealSrp1(UserDTO user) {
        SRP6ServerSession srpSession = srp6Helper.generateSRP6ServerSession();

        SrpStep1Res res = srp6Helper.doRealSrp1(srpSession, user);

        userSRPRepository.save(UserSRPEntity.builder()
                .userId(user.id())
                .email(user.email())
                .srpSession(srpSession)
                .build());

        return res;
    }

    private SrpStep1Res doFakeSrp1(UserDTO user) {
        SRP6ServerSession srpSession = srp6Helper.generateSRP6ServerSession();

        // Generating mock SrpStep1Res to prevent email enumeration in Pentest
        SrpStep1Res res = srp6Helper.doFakeSrp1(user.email(), srpSession);

        userSRPRepository.save(UserSRPEntity.builder()
                .userId(user.id())
                .email(user.email())
                .srpSession(srpSession)
                .build());

        return res;
    }

    private SrpStep1Res doFakeSrp1(String email) {
        SRP6ServerSession srpSession = srp6Helper.generateSRP6ServerSession();

        // Generating mock SrpStep1Res to prevent phoneNumber enumeration in Pentest

        return srp6Helper.doFakeSrp1(email, srpSession);
    }


}
