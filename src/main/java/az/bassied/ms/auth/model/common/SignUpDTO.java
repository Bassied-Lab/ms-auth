package az.bassied.ms.auth.model.common;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpDTO(
        @NotBlank(message = "sign.up.validation.firstName.is.blank")
        String firstName,
        @NotBlank(message = "sign.up.validation.lastName.is.blank")
        String lastName,
        @Email(message = "sign.up.validation.email.invalid",
                regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                flags = Pattern.Flag.CASE_INSENSITIVE)
        String email,
        @NotBlank(message = "sign.up.validation.verifier.is.blank")
        String verifier,
        @NotBlank(message = "sign.up.validation.salt.is.blank")
        String salt) {}

