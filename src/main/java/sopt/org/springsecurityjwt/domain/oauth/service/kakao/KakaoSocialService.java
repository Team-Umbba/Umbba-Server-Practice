package sopt.org.springsecurityjwt.domain.oauth.service.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sopt.org.springsecurityjwt.domain.oauth.controller.kakao.KakaoApiClient;
import sopt.org.springsecurityjwt.domain.oauth.controller.kakao.KakaoAuthApiClient;
import sopt.org.springsecurityjwt.domain.oauth.model.SocialPlatform;
import sopt.org.springsecurityjwt.domain.oauth.service.SocialService;
import sopt.org.springsecurityjwt.domain.oauth.model.SocialUser;
import sopt.org.springsecurityjwt.domain.oauth.repository.SocialUserRepository;
import sopt.org.springsecurityjwt.domain.oauth.dto.response.kakao.KakaoAccessTokenResponse;
import sopt.org.springsecurityjwt.domain.oauth.dto.response.kakao.KakaoUserResponse;
import sopt.org.springsecurityjwt.domain.oauth.dto.request.SocialLoginRequest;

@Service
@RequiredArgsConstructor
public class KakaoSocialService extends SocialService {

    @Value("${kakao.clientId}")
    private String clientId;

    private final SocialUserRepository socialUserRepository;

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    @Override
    public Long login(SocialLoginRequest request) {

        System.out.println(clientId);

        // Authorization code로 Access Token 불러오기
        KakaoAccessTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                "authorization_code",
                clientId,
                "http://localhost:8080/kakao/callback",
                request.getCode()
        );

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + tokenResponse.getAccessToken());

        SocialUser user = SocialUser.of(
                userResponse.getKakaoAccount().getProfile().getNickname(),
                userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                SocialPlatform.KAKAO,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );

        socialUserRepository.save(user);

        return user.getId();
    }
}