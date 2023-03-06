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
@Builder(toBuilder = true)
@RedisHash(value = "userSRP")
public class UserSRPEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    private Long userId;
    @ToString.Exclude
    private Object srpSession;
    @Value("${bucket.user.srp.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }

}
