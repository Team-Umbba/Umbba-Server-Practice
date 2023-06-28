package sopt.org.springsecurityjwt.domain.board.repository;

import org.springframework.data.repository.Repository;
import sopt.org.springsecurityjwt.domain.board.model.Board;

public interface BoardRepository extends Repository<Board, Long> {

    // CREATE
    void save(Board board);

    // READ


    // UPDATE

    // DELETE
}
