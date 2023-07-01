package sopt.org.springsecurityjwt.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.domain.board.dto.request.BoardRequestDto;
import sopt.org.springsecurityjwt.domain.board.model.Board;
import sopt.org.springsecurityjwt.domain.board.repository.BoardRepository;
import sopt.org.springsecurityjwt.domain.user.model.User;
import sopt.org.springsecurityjwt.error.ErrorType;
import sopt.org.springsecurityjwt.error.CustomException;
import sopt.org.springsecurityjwt.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void create(Long userId, BoardRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorType.INVALID_USER));

        Board newBoard = Board.newInstance(
                user,
                request.getTitle(),
                request.getContent(),
                request.getIsPublic()
        );

        boardRepository.save(newBoard);
    }
}
