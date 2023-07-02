package jjun.server.springsecurityjwt.domain.oauth.service;

import jjun.server.springsecurityjwt.common.dto.ApiResponse;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedRequestDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.TokenReissuedResponseDto;
import jjun.server.springsecurityjwt.domain.jwt.model.RefreshTokenRepository;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequestDto;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.UserLoginResponseDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.user.model.Role;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.Success;
import jjun.server.springsecurityjwt.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final SocialService socialService;
    private final KakaoLoginService kakaoLoginService;
//    private final AppleLoginService appleLoginService;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserLoginResponseDto login(String socialAccessToken, SocialLoginRequestDto request) {

        SocialPlatform socialPlatform = request.getSocialPlatform();
        String socialId = getSocialId(socialPlatform, socialAccessToken);

        // 이미 등록된 유저인지 검사
        boolean isRegistered = isUserBySocialAndSocialId(socialPlatform, socialId);
        if (!isRegistered) {
            User user = User.builder()
                    .role(Role.USER)
                    .socialPlatform(socialPlatform)
                    .socialId(socialId)
                    .build();

            userRepository.save(user);
        }

        User loginUser = getUserBySocialPlatformAndSocialId(socialPlatform, socialId);
        if (socialPlatform.equals(SocialPlatform.KAKAO)) {
            kakaoLoginService.setKakaoInfo(loginUser, socialAccessToken);
        }

        TokenDto tokenDto = jwtTokenProvider.issueToken(loginUser.getId());
        loginUser.updateRefreshToken(tokenDto.getRefreshToken());

        return UserLoginResponseDto.of(loginUser, tokenDto);
    }

    @Transactional
    public TokenReissuedResponseDto reissuedToken(final TokenReissuedRequestDto request) {

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
        return response;
    }

    /*@Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(Error.NO_EXISTS_USER)
        );
        refreshTokenRepository.deleteByUserId
    }*/


    private String getSocialId(SocialPlatform socialPlatform, String socialAccessToken) {

        log.info("socialPlatform: {}", socialPlatform);
        switch (socialPlatform.toString()) {
//            case "APPLE":
//                return appleLoginService.getAppleId(socialAccessToken);
            case "KAKAO":
                return kakaoLoginService.getKakaoId(socialAccessToken);
            default:
                throw new CustomException(Error.INVALID_SOCIAL_ACCESS_TOKEN);
        }
    }

    private boolean isUserBySocialAndSocialId(SocialPlatform socialPlatform, String socialId) {
        return userRepository.existsBySocialPlatformAndSocialId(socialPlatform, socialId);
    }

    private User getUserBySocialPlatformAndSocialId(SocialPlatform socialPlatform, String socialId) {
        return userRepository.findBySocialPlatformAndSocialId(socialPlatform, socialId).orElseThrow(
                () -> new CustomException(Error.NO_EXISTS_USER)
        );
    }
}
