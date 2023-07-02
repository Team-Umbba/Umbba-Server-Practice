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
public class KakaoLoginService extends SocialService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    /*@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;*/

    private final UserRepository userRepository;

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    // 인가 코드로 액세스 토큰 요청하기
    public String getKakaoAccessToken(String code) {

        KakaoAccessTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                GRANT_TYPE,
                CLIENT_ID,
                REDIRECT_URI,
                code
        );
        return "Bearer " + tokenResponse.getAccessToken();
    }

    public String getKakaoId(String socialAccessToken) {

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + socialAccessToken);

        return Long.toString(userResponse.getId());

    }

    public void setKakaoInfo(User loginUser, String socialAccessToken) {

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + socialAccessToken);

        loginUser.updateSocialInfo(
                userResponse.getKakaoAccount().getProfile().getNickname(),
                userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                socialAccessToken);
    }

    // TODO SocialService Interface를 이용하여 구현할 경우
    @Override
    public Long login(SocialLoginRequest request) {

        log.info("KakaoSocialService - login 수행, {}", request.getCode());

        String socialAccessToken = getKakaoAccessToken(request.getCode());

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + socialAccessToken);


        // Response -> Account -> Profile 로 접근
        User user = User.of(
                Role.USER,
                SocialPlatform.KAKAO,
                getKakaoId(socialAccessToken),
                userResponse.getKakaoAccount().getProfile().getNickname(),
                userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                socialAccessToken
                );

        userRepository.save(user);
        return user.getId();
    }
}
