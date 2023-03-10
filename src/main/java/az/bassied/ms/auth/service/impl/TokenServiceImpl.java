package az.bassied.ms.auth.service.impl;

import az.bassied.ms.auth.client.UserClient;
import az.bassied.ms.auth.dao.entities.UserSessionEntity;
import az.bassied.ms.auth.dao.repos.UserSessionRepository;
import az.bassied.ms.auth.error.exceptions.*;
import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.consts.Headers;
import az.bassied.ms.auth.model.consts.Messages;
import az.bassied.ms.auth.model.enums.TokenIssuer;
import az.bassied.ms.auth.model.enums.UserStatus;
import az.bassied.ms.auth.model.jwt.AccessTokenClaimsSet;
import az.bassied.ms.auth.model.jwt.AuthTokensDTO;
import az.bassied.ms.auth.model.jwt.RefreshTokenClaimsSet;
import az.bassied.ms.auth.model.jwt.TokensRequest;
import az.bassied.ms.auth.service.TokenService;
import az.bassied.ms.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final UserSessionRepository userSessionRepository;

    private final int accessTokenExpirationTime;
    private final int refreshTokenExpirationCount;
    private final int refreshTokenExpirationTime;

    public TokenServiceImpl(
            JwtUtil jwtUtil,
            UserClient userClient,
            UserSessionRepository userSessionRepository,
            @Value("${jwt.accessToken.expiration.time}") int accessTokenExpirationTime,
            @Value("${jwt.refreshToken.expiration.count}") int refreshTokenExpirationCount,
            @Value("${jwt.refreshToken.expiration.time}") int refreshTokenExpirationTime
    ) {
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
        this.userSessionRepository = userSessionRepository;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationCount = refreshTokenExpirationCount;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

    @Override
    public AuthTokensDTO generateTokens(String email, TokenIssuer issuer) {
        return generateTokens(new TokensRequest(email, issuer, refreshTokenExpirationCount));
    }

    private AuthTokensDTO generateTokens(TokensRequest tokensRequest) {
        logger.debug("Action.generateTokens.start");

        var accessTokenClaimsSet = createAccessTokenClaimsSet(tokensRequest.email(), tokensRequest);
        var refreshTokenClaimsSet = createRefreshTokenClaimsSet(tokensRequest.email(), tokensRequest);

        var keyPair = jwtUtil.generateKeyPair();

        ;

        userSessionRepository.save(new UserSessionEntity(tokensRequest.email(),
                accessTokenClaimsSet,
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded())));

        logger.debug("Action.generateTokens.debug saved session cache data");

        var accessToken = jwtUtil.generateToken(accessTokenClaimsSet, keyPair.getPrivate());
        var refreshToken = jwtUtil.generateToken(refreshTokenClaimsSet, keyPair.getPrivate());

        logger.debug("Action.generateTokens.end");
        return new AuthTokensDTO(accessToken, refreshToken);
    }

    @Override
    public AuthTokensDTO refreshTokens(String refreshToken) {
        logger.info("Action.refreshToken.start");

        var refreshTokenClaimsSet = jwtUtil.getClaimsFromRefreshToken(refreshToken);

        var email = refreshTokenClaimsSet.getEmail();
        try {
            MDC.put(Headers.USER_EMAIL, email);

            logger.debug("Action.refreshTokens.debug refreshTokenClaimsSet: {}", refreshTokenClaimsSet);

            UserSessionEntity sessionEntity = userSessionRepository.findById(email)
                    .orElseThrow(EmptyCacheException::new);


            PublicKey publicKey;
            TokenIssuer tokenIssuer;
            try {
                tokenIssuer = TokenIssuer.getEnum(sessionEntity.getAccessTokenClaimsSet().getIss());
                publicKey = KeyFactory.getInstance("RSA").generatePublic(
                        new X509EncodedKeySpec(Base64.getEncoder().encode(sessionEntity.getPublicKey().getBytes())));

                logger.debug("Action.refreshTokens.debug accessTokenClaims: {}", sessionEntity.getAccessTokenClaimsSet());
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                logger.error("Action.refreshTokens.error occurred while generating key", e);
                throw e;
            }

            jwtUtil.verifyToken(refreshToken, (RSAPublicKey) publicKey);
            if (jwtUtil.isRefreshTokenTimeExpired(refreshTokenClaimsSet)) {
                logger.warn("Action.refreshTokens.error refreshTokenTimeExpired");
                throw new RefreshTokenException();
            }

            if (jwtUtil.isRefreshTokenCountExpired(refreshTokenClaimsSet)) {
                logger.warn("Action.refreshTokens.error refreshTokenCountExpired");
                throw new RefreshTokenException();
            }

            TokensRequest tokensRequest = new TokensRequest(email, tokenIssuer,
                    jwtUtil.getClaimsFromRefreshToken(refreshToken).getCount() - 1);

            logger.info("Action.refreshToken.success");
            return generateTokens(tokensRequest);
        } catch (Exception ex) {
            logger.error("Action.refreshToken.fail ex: {}", ex.toString());
            throw new AuthException();
        }
    }

    @Override
    public UserDTO validateToken(String accessToken) {
        logger.info("Action.validateToken.start");
        try {
            var claimsSet = jwtUtil.getClaimsFromAccessToken(accessToken);
            var email = claimsSet.getEmail();
            MDC.put(Headers.USER_EMAIL, email);
            UserSessionEntity sessionEntity = userSessionRepository.findById(email)
                    .orElseThrow(TokenExpiredException::new);

            try {
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                        new X509EncodedKeySpec(Base64.getEncoder().encode(sessionEntity.getPublicKey().getBytes())));

                jwtUtil.verifyToken(accessToken, (RSAPublicKey) publicKey);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("Action.validateToken.error token verification failed");
                throw new TokenParsingException();
            }

            if (isTokenExpired(sessionEntity.getAccessTokenClaimsSet().getExpirationTime())) {
                logger.warn("Action.validateToken.warn token expired");
                throw new TokenExpiredException();
            }

            logger.info("Action.validateToken.success");
            return getUser(claimsSet.getEmail());
        } catch (TokenExpiredException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Action.validateToken.error occurred on validate token ex :{}", ex.toString());
            throw new AuthException();
        } finally {
            MDC.remove(Headers.USER_EMAIL);
        }
    }

    private UserDTO getUser(String email) {
        var user = userClient.getUserByEmail(email);
        if (user.status().equals(UserStatus.INACTIVE)) {
            logger.warn("Action.validateToken.warn this user status is {}", UserStatus.INACTIVE);
            throw new ForbiddenException(Messages.USER_INACTIVE_EXP, Messages.USER_INACTIVE_EXP_MSG);
        }
        return user;
    }

    private AccessTokenClaimsSet createAccessTokenClaimsSet(String email, TokensRequest request) {
        return AccessTokenClaimsSet.builder()
                .iss(request.tokenIssuer().getIssuer())
                .email(email)
                .createdTime(new Date())
                .expirationTime(jwtUtil.generateSessionExpirationTime(accessTokenExpirationTime))
                .build();
    }

    private RefreshTokenClaimsSet createRefreshTokenClaimsSet(String email, TokensRequest request) {
        return RefreshTokenClaimsSet.builder()
                .iss(request.tokenIssuer().getIssuer())
                .email(email)
                .expirationTime(jwtUtil.generateSessionExpirationTime(refreshTokenExpirationTime))
                .count(request.count())
                .build();
    }

    private boolean isTokenExpired(Date expirationTime) {
        return expirationTime.before(new Date());
    }
}
