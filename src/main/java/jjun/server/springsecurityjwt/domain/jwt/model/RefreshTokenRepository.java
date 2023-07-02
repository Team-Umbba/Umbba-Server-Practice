package jjun.server.springsecurityjwt.domain.jwt.model;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis Template 에서 Repository를 인터페이스로 정의하지 않고 직접 구현하는 방식을 사용하였다.
 */
@Repository
@AllArgsConstructor
public class RefreshTokenRepository {

    private RedisTemplate redisTemplate;

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getUserId(), Duration.ofDays(30));  // 30일 뒤에 메모리에서 삭제
//        redisTemplate.expire(refreshToken.getRefreshToken(), 60L, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long userId = valueOperations.get(refreshToken);

        if (Objects.isNull(userId)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, userId));
    }

    public void delete(final RefreshToken refreshToken) {
        redisTemplate.delete(refreshToken);
    }


}