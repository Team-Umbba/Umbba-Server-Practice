package sopt.org.springsecurityjwt.infrastructure;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.User;

import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    // CREATE
    void save(User user);

    // READ
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
    boolean existsByEmail(String email);

    // UPDATE

    // DELETE
}
