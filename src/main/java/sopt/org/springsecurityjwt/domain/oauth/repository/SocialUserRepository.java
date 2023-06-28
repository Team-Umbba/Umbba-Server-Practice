package sopt.org.springsecurityjwt.domain.oauth.repository;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.oauth.model.SocialUser;

public interface SocialUserRepository extends Repository<SocialUser, Long> {

    // CREATE
    void save(SocialUser socialUser);
}