package sopt.org.springsecurityjwt.infrastructure;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.Board;

public interface BoardRepository extends Repository<Board, Long> {

    // CREATE
    void save(Board board);

    // READ


    // UPDATE

    // DELETE
}
