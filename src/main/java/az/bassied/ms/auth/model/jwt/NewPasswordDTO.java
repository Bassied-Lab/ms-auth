package az.bassied.ms.auth.model.jwt;


import jakarta.validation.constraints.NotEmpty;

public record NewPasswordDTO(
        @NotEmpty(message = "new.pass.validation.salt.is.blank")
        String salt,
        @NotEmpty(message = "new.pass.validation.verifier.is.blank")
        String verifier) {
}
