package az.bassied.ms.auth.controller;

import az.bassied.ms.auth.error.exceptions.AuthException;
import az.bassied.ms.auth.model.consts.Headers;
import az.bassied.ms.auth.model.consts.URLs;
import az.bassied.ms.auth.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(URLs.VERIFY_ROOT)
public class AuthVerifyController {

    private final TokenService tokenService;

    @GetMapping
    public void verifyAccessToken(
            // For WEB requests
            @CookieValue(name = Headers.ACCESS_TOKEN, required = false) String cookieToken,
            // For mobile requests
            @RequestHeader(name = Headers.ACCESS_TOKEN, required = false) String headerToken,
            HttpServletResponse response
    ) {
        var token = Optional.ofNullable(cookieToken).orElse(headerToken);
        var accessToken = Optional.ofNullable(token).orElseThrow(AuthException::new);
        var user = tokenService.validateToken(accessToken);
        response.setHeader(Headers.USER_ID, String.valueOf(user.id()));
        response.setHeader(Headers.USER_ROLE, String.valueOf(user.role()));
    }

}
