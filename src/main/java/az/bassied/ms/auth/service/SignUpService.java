package az.bassied.ms.auth.service;

import az.bassied.ms.auth.model.common.SignUpDTO;
import org.springframework.stereotype.Service;


public interface SignUpService {
    void signUp(SignUpDTO request);

    void confirm(String token);
}
