package sopt.org.umbbaServer.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.board.dto.request.BoardRequestDto;
import sopt.org.umbbaServer.domain.board.service.BoardService;
import sopt.org.umbbaServer.domain.user.jwt.JwtProvider;
import sopt.org.umbbaServer.error.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(
            @RequestBody @Valid final BoardRequestDto request,
            Principal principal) {

        boardService.create(JwtProvider.getUserFromPrincial(principal), request);
        return ApiResponse.success(SuccessType.CREATE_BOARD_SUCCESS);
    }
}