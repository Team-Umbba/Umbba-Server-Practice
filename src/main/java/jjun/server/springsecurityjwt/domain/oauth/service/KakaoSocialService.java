package jjun.server.springsecurityjwt.domain.oauth.service;

import jjun.server.springsecurityjwt.domain.oauth.controller.KakaoApiClient;
import jjun.server.springsecurityjwt.domain.oauth.controller.KakaoAuthApiClient;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.KakaoAccessTokenResponse;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.KakaoUserResponse;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.user.model.Role;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Kakao Developer Docs에 명시된 필드 값을 가져와서 소셜 로그인을 하는 SocialUser에 대한 로직을 처리하는 클래스
 * 참고: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSocialService extends SocialService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    private final UserRepository userRepository;

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    @Override
    public Long login(SocialLoginRequest request) {

        log.info("KakaoSocialService - login 수행, {}", request.getCode());

        // 인가 코드로 액세스 토큰 요청하기
        KakaoAccessTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                "authorization_code",
                clientId,
                "http://localhost:9080/login/oauth2/code/kakao",
                request.getCode()
        );

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + tokenResponse.getAccessToken());

        // Response -> Account -> Profile 로 접근
        User user = User.of(
                userResponse.getKakaoAccount().getProfile().getEmail(),
                userResponse.getKakaoAccount().getProfile().getEmail(),
                userResponse.getKakaoAccount().getProfile().getNickname(),
                userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                SocialPlatform.KAKAO,
                userResponse.getKakaoAccount().getProfile().getEmail(), // TODO SocialId가 의미하는 바를 아직 정확히 몰라서 이메일 값으로 넣어둠
                Role.USER
                );

        userRepository.save(user);
        return user.getId();
    }
}
