package jjun.server.springsecurityjwt.domain.oauth.service;

import jjun.server.springsecurityjwt.domain.oauth.provider.CustomOAuth2User;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.oauth.provider.info.OAuth2Attributes;
import jjun.server.springsecurityjwt.domain.oauth.provider.info.OAuth2UserInfo;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 소셜 로그인 타입을 구별하기 위한 커스텀 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    private SocialPlatform getSocialPlatform(String registrationId) {
        if ("kakao".equals(registrationId)) {
            return SocialPlatform.KAKAO;
        }
        return SocialPlatform.APPLE;
    }

    private User getUser(OAuth2Attributes attributes, SocialPlatform socialPlatform) {
        User findUser = userRepository.findBySocialPlatformAndSocialId(
                socialPlatform,
                attributes.getOAuth2UserInfo().getProviderId()
        ).orElse(null);

        if (Objects.isNull(findUser)) {
            return saveUser(attributes, socialPlatform);
        }
        return findUser;
    }

    private User saveUser(OAuth2Attributes attributes, SocialPlatform socialPlatform) {
        User newUser = attributes.toUserEntity(
                socialPlatform,
                attributes.getOAuth2UserInfo()
        );
        return userRepository.save(newUser);
    }

    /**
     * 서드파티에 사용자 정보를 요청할 수 있는 Access Token 을 얻은 후에 수행되는 메서드
     *
     * 여기서 하는 작업
     * 1. access token을 이용해 서드파티 서버로부터 사용자 정보를 받아온다.
     * 2. 해당 사용자가 이미 회원가입이 되어 있는 사용자인지 확인한다.
     * 3. 만약 회원가입이 되어 있지 않다면, 회원가입 처리한다.
     * 4. 만약 회원가입이 되어 있다면, 프로필 사진 URL 등의 변경된 정보를 업데이트한다.
     *
     * -> UserPrincipal 을 return 한다.
     * *세션 방식에서는 여기서 return한 객체가 시큐리티 세션에 저장되지만, 우리는 토큰 방식으로 구현하므로 이를 저장하지 않는다.
     * (JWT 방식에서는 인증&인가 수행시 HttpSession을 사용X)
     *
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() : OAuth2 로그인 요청");

        /**
         * DefaultOAuth2UserService.loadUser(userRequest) -> DefaultOAuth2User 객체를 생성 & 반환
         *    - 소셜 로그인 API의 사용자 정보 제공 uri로 요청을 보내서 사용자 정보를 가져온다 -> DefaultOAuth2User 객체를 생성 & 반환
         *    - OAuth2User: OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);


        /**
         * userRequest -> registrationId 추출 & registrationId를 통해 해당하는 SocialType 저장
         * ex. http://localhost:8080/oauth2/authorization/kakao -> registrationId: kakao
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialPlatform socialPlatform = getSocialPlatform(registrationId);

        // 소셜 로그인 API에서 제공하는 사용자 정보 JSON 값
        Map<String, Object> attributes = oAuth2User.getAttributes();

        /**
         * OAuth2 로그인 시 키(PK)가 되는 값: userNameAttributeName
         * userNameAttributeName -> attributeKey로 설정된다
         */
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(
                socialPlatform,
                userNameAttributeName,
                attributes
        );

        User oauthUser = getUser(oAuth2Attributes, socialPlatform);  // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2USer 객체를 생성해서 반환
        return new CustomOAuth2User(
                // 가지고 있는 권한들로 이루어지는 Set 객체 생성
                Collections.singleton(new SimpleGrantedAuthority(oauthUser.getRole().getRole())),
                attributes,
                oAuth2Attributes.getAttributeKey(),
                oauthUser.getEmail(),
                oauthUser.getRole()
        );


    }

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
//    private User getUser(OAuth2Attributes attributes, SocialPlatform socialPlatform) {
//        User findUser = userRepository.findBySocialPlatformAndSocialId(
//                socialPlatform
//                , attributes.getOauth2UserInfo().getProviderId()).orElse(null);
//
//        if(findUser == null) {
//            return saveUser(attributes, socialPlatform);
//        }
//        return findUser;
//    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */

}
