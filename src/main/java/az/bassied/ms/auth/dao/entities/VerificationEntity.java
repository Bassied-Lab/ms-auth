package az.bassied.ms.auth.dao.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@RedisHash(value = "verification")
public class VerificationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String token;
    private String email;
    @Value("${bucket.verification.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }

    public VerificationEntity(String token, String email) {
        this.token = token;
        this.email = email;
    }
}