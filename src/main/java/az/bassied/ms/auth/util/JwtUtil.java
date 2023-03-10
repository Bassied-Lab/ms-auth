package az.bassied.ms.auth.util;

import az.bassied.ms.auth.error.exceptions.AuthException;
import az.bassied.ms.auth.error.exceptions.TokenGenerationException;
import az.bassied.ms.auth.error.exceptions.TokenParsingException;
import az.bassied.ms.auth.model.jwt.AccessTokenClaimsSet;
import az.bassied.ms.auth.model.jwt.RefreshTokenClaimsSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
            keyPairGen.initialize(KEY_SIZE);

            return keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Action.generateKeyPair.error No such algorithm", e);
            throw new AuthException();
        }
    }

    public String generateToken(AccessTokenClaimsSet accessTokenClaimsSet, PrivateKey privateKey) {
        SignedJWT signedJWT;
        try {
            var json = objectMapper.writeValueAsString(accessTokenClaimsSet);
            logger.debug("Action.generateToken.debug accessTokenJson: {}", json);
            signedJWT = generateSignedJWT(json, privateKey);
        } catch (Exception e) {
            logger.error("Action.generateToken.error cannot generate access token", e);
            throw new TokenGenerationException();
        }
        return signedJWT.serialize();
    }

    public String generateToken(RefreshTokenClaimsSet refreshTokenClaimsSet, PrivateKey privateKey) {
        SignedJWT signedJWT;
        try {
            signedJWT = generateSignedJWT(objectMapper.writeValueAsString(refreshTokenClaimsSet), privateKey);
        } catch (Exception e) {
            logger.error("Action.generateToken.error cannot generate refresh token", e);
            throw new TokenGenerationException();
        }
        return signedJWT.serialize();
    }

    public void verifyToken(String token, RSAPublicKey publicKey) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJwt.verify(verifier)) {
                throw new TokenParsingException();
            }
        } catch (ParseException | JOSEException e) {
            logger.error("Action.verifyToken.error can't parse token ", e);
            throw new TokenParsingException();
        }
    }

    public Date generateSessionExpirationTime(Integer expirationSeconds) {
        return new Date(System.currentTimeMillis() + expirationSeconds * 1000);
    }

    public boolean isRefreshTokenTimeExpired(RefreshTokenClaimsSet refreshTokenClaimsSet) {
        final Date expiration = refreshTokenClaimsSet.getExpirationTime();
        return expiration.before(new Date());
    }

    public boolean isRefreshTokenCountExpired(RefreshTokenClaimsSet refreshTokenClaimsSet) {
        return refreshTokenClaimsSet.getCount() <= 0;
    }

    public AccessTokenClaimsSet getClaimsFromAccessToken(String token) {
        AccessTokenClaimsSet claimsSet;
        try {
            claimsSet = objectMapper.readValue(getClaimsFromToken(token).toString(), AccessTokenClaimsSet.class);
        } catch (IOException | ParseException e) {
            logger.error("Action.getClaimsFromAccessToken.error can't parse access token", e);
            throw new TokenParsingException();
        }
        return claimsSet;
    }

    public RefreshTokenClaimsSet getClaimsFromRefreshToken(String token) {
        RefreshTokenClaimsSet claimsSet;
        try {
            claimsSet = objectMapper.readValue(getClaimsFromToken(token).toString(), RefreshTokenClaimsSet.class);
        } catch (IOException | ParseException e) {
            logger.error("Action.getClaimsFromRefreshToken.error can't parse refresh token", e);
            throw new TokenParsingException();
        }
        return claimsSet;
    }

    private JWTClaimsSet getClaimsFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet();
    }

    private SignedJWT generateSignedJWT(String tokenClaimSetJson, PrivateKey privateKey)
            throws JOSEException, ParseException {

        JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(tokenClaimSetJson);
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        RSASSASigner signer = new RSASSASigner(privateKey);
        signedJWT.sign(signer);

        return signedJWT;
    }
}
