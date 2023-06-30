package sopt.org.springsecurityjwt.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;
import sopt.org.springsecurityjwt.domain.jwt.model.RefreshToken;

//Redis에 저장해주는 역할
public interface TokenRepository extends CrudRepository<RefreshToken, Long> {
}
