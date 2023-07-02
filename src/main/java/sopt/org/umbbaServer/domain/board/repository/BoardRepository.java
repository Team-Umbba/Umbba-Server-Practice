package sopt.org.umbbaServer.domain.board.repository;

import org.springframework.data.repository.Repository;
import sopt.org.umbbaServer.domain.board.model.Board;

public interface BoardRepository extends Repository<Board, Long> {

    // CREATE
    void save(Board board);

    // READ


    // UPDATE

    // DELETE
}
