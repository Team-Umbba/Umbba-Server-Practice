package sopt.org.springsecurityjwt.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.jwt.provider.JwtProvider;
import sopt.org.springsecurityjwt.domain.user.dto.request.SocialLoginRequestDto;
import sopt.org.springsecurityjwt.domain.user.dto.response.UserLoginResponseDto;
import sopt.org.springsecurityjwt.domain.user.service.SocialService;
import sopt.org.springsecurityjwt.domain.user.service.SocialServiceProvider;
import sopt.org.springsecurityjwt.exception.dto.ApiResponse;
import sopt.org.springsecurityjwt.exception.Success;
import sopt.org.springsecurityjwt.domain.user.dto.request.SocialLoginRequest;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final SocialServiceProvider socialServiceProvider;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ApiResponse<UserLoginResponseDto> login(@RequestHeader("code") String code, @RequestBody SocialLoginRequestDto request) {
        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return ApiResponse.success(Success.SOCIAL_LOGIN_SUCCESS, socialService.login(SocialLoginRequest.of(code)));
    }

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenDto> refresh(@RequestBody TokenDto token) throws Exception {
        return ApiResponse.success(Success.REFRESH_SUCCESS, jwtProvider.refreshAccessToken(token));
    }
}