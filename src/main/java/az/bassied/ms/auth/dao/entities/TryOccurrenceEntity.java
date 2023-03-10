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
@RedisHash(value = "tryOccurrence")
public class TryOccurrenceEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Id
    private String email;
    private Integer count;
    @Value("${bucket.incorrectCredentialTry.expire.ttl}")
    private long ttl;
    @TimeToLive
    public long getTimeToLive() {
        return ttl;
    }

    public TryOccurrenceEntity(String email, Integer count) {
        this.email = email;
        this.count = count;
    }
}

