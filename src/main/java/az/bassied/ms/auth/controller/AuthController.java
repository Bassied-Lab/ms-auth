package az.bassied.ms.auth.controller;

import az.bassied.ms.auth.error.exceptions.AuthException;
import az.bassied.ms.auth.model.consts.Headers;
import az.bassied.ms.auth.model.consts.URLs;
import az.bassied.ms.auth.model.jwt.NewPasswordDTO;
import az.bassied.ms.auth.model.srp.SrpStep1Req;
import az.bassied.ms.auth.model.srp.SrpStep1Res;
import az.bassied.ms.auth.model.srp.SrpStep2Req;
import az.bassied.ms.auth.model.srp.SrpStep2Res;
import az.bassied.ms.auth.service.AuthService;
import az.bassied.ms.auth.service.TokenService;
import az.bassied.ms.auth.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping(URLs.ROOT)
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @PostMapping(URLs.SRP1)
    public ResponseEntity<SrpStep1Res> srpAuthStep1(@RequestBody @NotNull SrpStep1Req req) {
        return ResponseEntity.ok(authService.srpAuthStep1(req));
    }

    @PostMapping(URLs.SRP2)
    public ResponseEntity<SrpStep2Res> srpAuthStep2(
            @CookieValue(name = Headers.COOKIE_IDENTITY, required = false) String identity,
            @RequestBody @NotNull SrpStep2Req req,
            HttpServletResponse response
    ) {
        var srpResponse = authService.srpAuthStep2(req);
        cookieUtil.addCookies(srpResponse.getSecond(), identity, response);
        response.addHeader(Headers.ACCESS_TOKEN, srpResponse.getSecond().accessToken());
        response.addHeader(Headers.REFRESH_TOKEN, srpResponse.getSecond().refreshToken());
        return ResponseEntity.ok(srpResponse.getFirst());
    }

    @PostMapping(URLs.CHANGE_PASS)
    public void changePassword(
            @CookieValue(name = Headers.COOKIE_IDENTITY, required = false) String identity,
            @CookieValue(name = Headers.ACCESS_TOKEN, required = false) String cookieToken,
            @RequestHeader(name = Headers.ACCESS_TOKEN, required = false) String headerToken,
            @RequestBody @NotNull NewPasswordDTO request,
            HttpServletResponse response
    ) {
        var token = Optional.ofNullable(cookieToken).orElse(headerToken);
        var accessToken = Optional.ofNullable(token).orElseThrow(AuthException::new);
        var tokens = authService.changePassword(accessToken, request);
        cookieUtil.addCookies(tokens, identity, response);
        response.addHeader(Headers.ACCESS_TOKEN, tokens.accessToken());
        response.addHeader(Headers.REFRESH_TOKEN, tokens.refreshToken());
    }


    @PostMapping(URLs.LOGOUT)
    public void logout(
            @CookieValue(name = Headers.ACCESS_TOKEN, required = false) String cookieToken,
            @RequestHeader(name = Headers.ACCESS_TOKEN, required = false) String headerToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException {
        var token = Optional.ofNullable(cookieToken).orElse(headerToken);
        var accessToken = Optional.ofNullable(token).orElseThrow(AuthException::new);
        authService.logout(accessToken);
        cookieUtil.removeCookies(response);
        request.getSession().invalidate();
        request.logout();
    }


    @PostMapping(URLs.REFRESH_TOKEN)
    public void refreshToken(
            @CookieValue(name = Headers.COOKIE_IDENTITY, required = false) String identity,
            @CookieValue(name = Headers.REFRESH_TOKEN, required = false) String cookieToken,
            @RequestHeader(name = Headers.REFRESH_TOKEN, required = false) String headerToken,
            HttpServletResponse response
    ) {
        var token = Optional.ofNullable(cookieToken).orElse(headerToken);
        var refreshToken = Optional.ofNullable(token).orElseThrow(AuthException::new);
        var tokens = tokenService.refreshTokens(refreshToken);
        cookieUtil.addCookies(tokens, identity, response);
        response.addHeader(Headers.ACCESS_TOKEN, tokens.accessToken());
        response.addHeader(Headers.REFRESH_TOKEN, tokens.refreshToken());
    }
}