package jjun.server.springsecurityjwt.domain.oauth.controller;

import jjun.server.springsecurityjwt.common.dto.ApiResponse;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedRequestDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedResponseDto;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequestDto;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.UserLoginResponseDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialServiceProvider;
import jjun.server.springsecurityjwt.domain.oauth.service.AuthService;
import jjun.server.springsecurityjwt.domain.oauth.service.KakaoLoginService;
import jjun.server.springsecurityjwt.domain.oauth.service.SocialService;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.Success;
import jjun.server.springsecurityjwt.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authorization의 기본은 Header에 정보를 담아서 요청을 보내는 것을 권장!
 * => code를 헤더에 실어서 보내도록 구현하자 (이 code는 단발성이기 떄문에 한 번 잘못되면 다시 발급받도록 해야 함)
 *
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SocialServiceProvider socialServiceProvider;
    private final AuthService authService;
    private final KakaoLoginService kakaoLoginService;  // TODO 클라한테 넘겨받으므로 빼도 되는 부분

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserLoginResponseDto> login(
            @RequestHeader("Authorization") String socialAccessToken,
            @RequestBody SocialLoginRequestDto request) {

//        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return ApiResponse.success(Success.SOCIAL_LOGIN_SUCCESS, authService.login(socialAccessToken, request));
    }

    @PostMapping("/reissued")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenReissuedResponseDto> reissued(@RequestBody @Valid final TokenReissuedRequestDto request) {
        return ApiResponse.success(Success.AUTHORIZATION_SUCCESS, authService.reissuedToken(request));
    }


    @PostMapping("/kakao")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse kakaoAccessToken(
            @RequestHeader("Authorization") String code) {

        return ApiResponse.success(Success.KAKAO_ACCESS_TOKEN_SUCCESS, kakaoLoginService.getKakaoAccessToken(code));
    }


}
