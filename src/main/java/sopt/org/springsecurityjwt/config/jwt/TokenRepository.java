package sopt.org.springsecurityjwt.config.jwt;

import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {
}
