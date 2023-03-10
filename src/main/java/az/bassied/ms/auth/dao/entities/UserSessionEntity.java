package az.bassied.ms.auth.dao.entities;

import az.bassied.ms.auth.model.jwt.AccessTokenClaimsSet;
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
@RedisHash(value = "userSession")
public class UserSessionEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    private AccessTokenClaimsSet accessTokenClaimsSet;
    private String publicKey;
    @Value("${bucket.user.session.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }

    public UserSessionEntity(String email, AccessTokenClaimsSet accessTokenClaimsSet, String publicKey) {
        this.email = email;
        this.accessTokenClaimsSet = accessTokenClaimsSet;
        this.publicKey = publicKey;
    }
}