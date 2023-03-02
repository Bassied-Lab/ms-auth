package az.bassied.ms.auth.model.common;

import az.bassied.ms.auth.model.enums.UserRole;
import az.bassied.ms.auth.model.enums.UserStatus;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        UserRole role,
        String verifier,
        String salt) {
}
