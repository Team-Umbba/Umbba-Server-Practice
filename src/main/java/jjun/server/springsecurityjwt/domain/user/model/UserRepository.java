package jjun.server.springsecurityjwt.domain.user.model;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    boolean existsBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId);
    boolean existsByUsername(String username);


}
