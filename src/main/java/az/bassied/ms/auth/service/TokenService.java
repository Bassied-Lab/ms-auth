package az.bassied.ms.auth.service;

import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.enums.TokenIssuer;
import az.bassied.ms.auth.model.jwt.AuthTokensDTO;

public interface TokenService {
    AuthTokensDTO generateTokens(String email, TokenIssuer issuer);

    AuthTokensDTO refreshTokens(String refreshToken);

    UserDTO validateToken(String accessToken);
}
