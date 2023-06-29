package sopt.org.springsecurityjwt.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.springsecurityjwt.domain.board.dto.request.BoardRequestDto;
import sopt.org.springsecurityjwt.domain.board.service.BoardService;
import sopt.org.springsecurityjwt.exception.dto.ApiResponse;
import sopt.org.springsecurityjwt.config.resolver.UserId;
import sopt.org.springsecurityjwt.exception.Success;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(
            @UserId String email,
            @RequestBody @Valid final BoardRequestDto request) {
        boardService.create(email, request);
        return ApiResponse.success(Success.CREATE_BOARD_SUCCESS);
    }
}