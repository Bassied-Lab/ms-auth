package az.bassied.ms.auth.dao.entities;

import com.nimbusds.srp6.SRP6ServerSession;
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
public class UserSRPEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    private Long userId;
    @ToString.Exclude
    private SRP6ServerSession srpSession;
    @Value("${bucket.user.srp.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }

    public UserSRPEntity(String email, Long userId, SRP6ServerSession srpSession){
        this.email = email;
        this.userId = userId;
        this.srpSession = srpSession;
    }
}
