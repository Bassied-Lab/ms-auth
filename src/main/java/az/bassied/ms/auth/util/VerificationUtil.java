package az.bassied.ms.auth.util;

import az.bassied.ms.auth.service.impl.SignUpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class VerificationUtil {

    private static final Logger logger = LoggerFactory.getLogger(SignUpServiceImpl.class);


    public String generateToken() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
