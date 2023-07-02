package jjun.server.springsecurityjwt.domain.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@RedisHash(value = "refreshToken")
@AllArgsConstructor
public class RefreshToken {

    public RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    @Id
    private String refreshToken;

    private Long userId;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Integer expiration;  // Redis에서 만료시간이 되면 알아서 삭제 (서버에서 일일이 확인하는 작업의 부담을 덜어주는 역할)


}
