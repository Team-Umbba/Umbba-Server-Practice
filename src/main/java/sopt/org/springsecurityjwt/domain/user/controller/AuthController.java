package sopt.org.springsecurityjwt.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.springsecurityjwt.domain.user.jwt.JwtProvider;
import sopt.org.springsecurityjwt.domain.user.service.AuthService;
import sopt.org.springsecurityjwt.domain.user.jwt.TokenDto;
import sopt.org.springsecurityjwt.domain.user.dto.SocialLoginRequestDto;
import sopt.org.springsecurityjwt.domain.user.dto.UserLoginResponseDto;
import sopt.org.springsecurityjwt.domain.user.social.kakao.KakaoLoginService;
import sopt.org.springsecurityjwt.error.ApiResponse;
import sopt.org.springsecurityjwt.error.SuccessType;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserLoginResponseDto> login(
            @RequestHeader("Authorization") String socialAccessToken,
            @RequestBody SocialLoginRequestDto request) throws NoSuchAlgorithmException, InvalidKeySpecException {

        UserLoginResponseDto response = authService.login(socialAccessToken, request);
        return ApiResponse.success(SuccessType.LOGIN_SUCCESS, response);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenDto> refresh(
            @RequestHeader("Authorization") String refreshToken) throws Exception {

        return ApiResponse.success(SuccessType.REFRESH_SUCCESS, authService.refreshToken(refreshToken));
    }

    @PostMapping("/log-out") //Spring Security 자체 로그아웃과 충돌하기 때문에 이렇게 써줌
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse logout(Principal principal) {

        authService.logout(JwtProvider.getUserFromPrincial(principal));
        return ApiResponse.success(SuccessType.LOGOUT_SUCCESS);
    }

    @PostMapping("/kakao")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse kakaoAccessToken(
            @RequestHeader("Authorization") String code) {

        return ApiResponse.success(SuccessType.KAKAO_ACCESS_TOKEN_SUCCESS, kakaoLoginService.getKakaoAccessToken(code));
    }
}