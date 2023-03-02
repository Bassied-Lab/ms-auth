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
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@RedisHash(value = "accountLock")
public class AccountLockEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    private LocalDateTime lockExpireDate;
    @Value("${bucket.account.lock.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }
}
