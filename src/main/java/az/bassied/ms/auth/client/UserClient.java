package az.bassied.ms.auth.client;

import az.bassied.ms.auth.model.common.SignUpDTO;
import az.bassied.ms.auth.model.common.UserDTO;
import az.bassied.ms.auth.model.jwt.NewPasswordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user")
public interface UserClient {
    @PostMapping
    UserDTO create(SignUpDTO request);

    @PatchMapping("/activate")
    UserDTO activateUserByEmail(@RequestParam String email);

    @GetMapping("/email")
    UserDTO getUserByEmail(@RequestParam String email);

    @PatchMapping("/change-password/{id}")
    void changePassword(@PathVariable Long id, @RequestBody NewPasswordDTO newPasswordDTO);
}
