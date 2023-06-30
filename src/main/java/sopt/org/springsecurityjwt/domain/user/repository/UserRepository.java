package sopt.org.springsecurityjwt.domain.user.repository;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.user.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    // CREATE
    void save(User user);

    // READ
    Optional<User> findById(Long id);
    Optional<User> findBySocialId(Long socialId);

    // UPDATE

    // DELETE
}
