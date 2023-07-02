package jjun.server.springsecurityjwt.domain.oauth.service;

import jjun.server.springsecurityjwt.domain.jwt.dto.TokenDto;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequestDto;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.UserLoginResponseDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final SocialService socialService;
    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;

    private final UserRepository userRepository;

    @Transactional
    public UserLoginResponseDto login(String socialAccessToken, SocialLoginRequestDto request) {

        SocialPlatform socialPlatform = request.getSocialPlatform();
        String socialId = getSocialId(socialPlatform, socialAccessToken);

        // 이미 등록된 유저인지 검사
        boolean isRegistered = isUserBySocialAndSocialId(socialPlatform, socialId);
        if (!isRegistered) {
            User user = User.builder()
                    .socialPlatform(socialPlatform)
                    .socialNickname(socialId)
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


    private String getSocialId(SocialPlatform socialPlatform, String socialAccessToken) {

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
