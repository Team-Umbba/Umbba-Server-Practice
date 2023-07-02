package sopt.org.umbbaServer.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.board.dto.request.BoardRequestDto;
import sopt.org.umbbaServer.domain.board.model.Board;
import sopt.org.umbbaServer.domain.board.repository.BoardRepository;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.error.ErrorType;
import sopt.org.umbbaServer.error.CustomException;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;

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
