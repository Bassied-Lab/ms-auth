package az.bassied.ms.auth.dao.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

//todo extract to properties
@RedisHash(value = "verification", timeToLive = 90)
public record VerificationEntity(@Id String token, String email) {
}
