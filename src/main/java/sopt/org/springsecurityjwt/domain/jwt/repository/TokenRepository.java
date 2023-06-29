package sopt.org.springsecurityjwt.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import sopt.org.springsecurityjwt.domain.jwt.model.RefreshToken;

public interface TokenRepository extends CrudRepository<RefreshToken, Long> {
}
