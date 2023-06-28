package sopt.org.springsecurityjwt.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import sopt.org.springsecurityjwt.domain.jwt.model.Token;

public interface TokenRepository extends CrudRepository<Token, Long> {
}
