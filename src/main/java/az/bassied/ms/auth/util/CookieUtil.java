package az.bassied.ms.auth.util;

import az.bassied.ms.auth.model.jwt.AuthTokensDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static az.bassied.ms.auth.model.consts.Headers.ACCESS_TOKEN;
import static az.bassied.ms.auth.model.consts.Headers.COOKIE_IDENTITY;
import static az.bassied.ms.auth.model.consts.Headers.REFRESH_TOKEN;


@Component
public class CookieUtil {
    private static final String COOKIE_PATH = "/";
    public static final String STRICT = "Strict";

    private final int tokensExpirationTime;

    public CookieUtil(@Value("${jwt.refreshToken.expiration.time}") int tokensExpirationTime) {
        this.tokensExpirationTime = tokensExpirationTime;
    }

    public void addCookies(AuthTokensDTO authTokens, String identity, HttpServletResponse response) {
        addCookie(ACCESS_TOKEN, authTokens.accessToken(), response);
        addCookie(REFRESH_TOKEN, authTokens.refreshToken(), response);
        if (identity == null)
            addIdentity(authTokens.accessToken(), response);
    }

    public void removeCookies(HttpServletResponse response) {
        removeCookie(ACCESS_TOKEN, response);
        removeCookie(REFRESH_TOKEN, response);
    }

    private void addIdentity(String token, HttpServletResponse response) {
        String[] chunks = token.split("\\.");
        var identity = chunks[1];
        final ResponseCookie responseCookie = ResponseCookie
                .from(COOKIE_IDENTITY, identity)
                .secure(true)
                .httpOnly(true)
                .path(COOKIE_PATH)
                .maxAge(Duration.ofDays(365))
                .sameSite(STRICT)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void addCookie(String name, String value, HttpServletResponse response) {
        final ResponseCookie responseCookie = ResponseCookie
                .from(name, value)
                .secure(true)
                .httpOnly(true)
                .path(COOKIE_PATH)
                .maxAge(tokensExpirationTime)
                .sameSite(STRICT)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void removeCookie(String name, HttpServletResponse response) {
        final ResponseCookie responseCookie = ResponseCookie
                .from(name, null)
                .secure(true)
                .httpOnly(true)
                .path(COOKIE_PATH)
                .maxAge(0)
                .sameSite(STRICT)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }
}
