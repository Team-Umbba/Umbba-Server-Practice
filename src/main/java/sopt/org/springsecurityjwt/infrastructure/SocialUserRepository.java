package sopt.org.springsecurityjwt.infrastructure;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.SocialUser;

public interface SocialUserRepository extends Repository<SocialUser, Long> {

    // CREATE
    void save(SocialUser socialUser);
}