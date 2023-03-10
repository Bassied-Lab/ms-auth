package az.bassied.ms.auth.controller;

import az.bassied.ms.auth.model.common.SignUpDTO;
import az.bassied.ms.auth.model.consts.URLs;
import az.bassied.ms.auth.service.SignUpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(URLs.SIGN_UP_ROOT)
public class SignUpController {

    private final SignUpService service;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpDTO request) {
        service.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping(URLs.SIGN_UP_CONFIRM)
    public void confirm(@PathVariable @NotNull String token) {
        service.confirm(token);
    }
}