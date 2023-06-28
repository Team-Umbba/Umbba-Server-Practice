package sopt.org.springsecurityjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.controller.dto.request.BoardRequestDto;
import sopt.org.springsecurityjwt.domain.Board;
import sopt.org.springsecurityjwt.domain.User;
import sopt.org.springsecurityjwt.exception.Error;
import sopt.org.springsecurityjwt.exception.model.NotFoundException;
import sopt.org.springsecurityjwt.infrastructure.BoardRepository;
import sopt.org.springsecurityjwt.infrastructure.UserRepository;

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
