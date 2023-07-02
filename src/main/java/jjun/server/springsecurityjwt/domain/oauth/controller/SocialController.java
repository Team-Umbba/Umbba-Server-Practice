package jjun.server.springsecurityjwt.domain.oauth.controller;

import jjun.server.springsecurityjwt.common.dto.ApiResponse;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedRequestDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedResponseDto;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequestDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialServiceProvider;
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
@RequestMapping("/social")
public class SocialController {

    private final SocialServiceProvider socialServiceProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public Long login(@RequestHeader("code") String code, @RequestBody SocialLoginRequestDto request) {

        // TODO 이미 가입된 유저인지 확인하는 절차 추가
        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return socialService.login(SocialLoginRequest.of(code));
    }

    @PostMapping("/reissued")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenReissuedResponseDto> reissued(@RequestBody @Valid final TokenReissuedRequestDto request) {

        // Refresh Token 만료 검증 -> 만료 시 재로그인을 해야 함
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new CustomException(Error.EXPIRED_JWT_TOKEN);
        }

        // RefreshToken을 이용해 토큰 재발급
        TokenReissuedResponseDto response = jwtTokenProvider.reissueToken(request);
        Long userId = jwtTokenProvider.getUserFromJwt(response.getAccessToken());
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(Error.NO_EXISTS_USER)
        );
        user.updateRefreshToken(response.getRefreshToken());
        return ApiResponse.success(Success.AUTHORIZATION_SUCCESS, response);
    }




}
