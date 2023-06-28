package sopt.org.springsecurityjwt.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.domain.board.dto.request.BoardRequestDto;
import sopt.org.springsecurityjwt.domain.board.model.Board;
import sopt.org.springsecurityjwt.domain.board.repository.BoardRepository;
import sopt.org.springsecurityjwt.domain.user.model.User;
import sopt.org.springsecurityjwt.exception.Error;
import sopt.org.springsecurityjwt.exception.model.NotFoundException;
import sopt.org.springsecurityjwt.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void create(String email, BoardRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(Error.NOT_FOUND_USER_EXCEPTION, Error.NOT_FOUND_USER_EXCEPTION.getMessage()));

        Board newBoard = Board.newInstance(
                user,
                request.getTitle(),
                request.getContent(),
                request.getIsPublic()
        );

        boardRepository.save(newBoard);
    }
}
