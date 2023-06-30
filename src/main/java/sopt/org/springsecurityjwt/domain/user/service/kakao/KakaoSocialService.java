package sopt.org.springsecurityjwt.domain.user.service.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.jwt.provider.JwtProvider;
import sopt.org.springsecurityjwt.domain.user.controller.kakao.KakaoApiClient;
import sopt.org.springsecurityjwt.domain.user.controller.kakao.KakaoAuthApiClient;
import sopt.org.springsecurityjwt.domain.user.dto.response.UserLoginResponseDto;
import sopt.org.springsecurityjwt.domain.user.model.SocialPlatform;
import sopt.org.springsecurityjwt.domain.user.service.SocialService;
import sopt.org.springsecurityjwt.domain.user.dto.response.kakao.KakaoAccessTokenResponse;
import sopt.org.springsecurityjwt.domain.user.dto.response.kakao.KakaoUserResponse;
import sopt.org.springsecurityjwt.domain.user.dto.request.SocialLoginRequest;
import sopt.org.springsecurityjwt.domain.user.model.Authority;
import sopt.org.springsecurityjwt.domain.user.model.User;
import sopt.org.springsecurityjwt.domain.user.repository.UserRepository;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoSocialService extends SocialService {

    @Value("${kakao.clientId}")
    private String clientId;
    @Value("${kakao.authorization-grant-type}")
    private String grantType;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    @Override
    public UserLoginResponseDto login(SocialLoginRequest request) {

        // Authorization code로 Access Token 불러오기
        KakaoAccessTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                grantType,
                clientId,
                redirectUri,
                request.getCode()
        );

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + tokenResponse.getAccessToken());

        Long socialId = userResponse.getId();
        User loginUser = userRepository.findBySocialId(socialId).orElseGet(
                () -> { //신규 회원 가입의 경우이므로 User 객체 생성
                    User newUser = User.builder()
                                        .socialPlatform(SocialPlatform.KAKAO)
                                        .socialId(socialId).build();
                    newUser.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
                    userRepository.save(newUser);
                    return newUser;
                }
        );

        loginUser.updateSocialInfo(userResponse.getKakaoAccount().getProfile().getNickname(),
                                  userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                                  tokenResponse.getAccessToken(), tokenResponse.getRefreshToken()); //Kakao의 Access 토큰과 Refresh 토큰도 매번 업데이트

        //로그인마다 서비스 자체의 Refresh Token 새로 발급
        loginUser.setRefreshToken(jwtProvider.createRefreshToken(loginUser));

        return UserLoginResponseDto.builder()
                .userId(loginUser.getId())
                .username(loginUser.getUsername())
                .gender(loginUser.getGender())
                .bornYear(loginUser.getBornYear())
                .roles(loginUser.getRoles())
                .token(TokenDto.builder()
                        .accessToken(jwtProvider.createAccessToken(Long.toString(loginUser.getId()), loginUser.getRoles()))
                        .refreshToken(loginUser.getRefreshToken()).build())
                .socialPlatform(loginUser.getSocialPlatform())
                .socialNickname(loginUser.getSocialNickname())
                .socialProfileImage(loginUser.getSocialProfileImage())
                .socialAccessToken(loginUser.getSocialAccessToken())
                .socialRefreshToken(loginUser.getSocialRefreshToken())
                .build();
    }
}