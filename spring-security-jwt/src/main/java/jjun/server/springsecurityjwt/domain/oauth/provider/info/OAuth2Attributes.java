package jjun.server.springsecurityjwt.domain.oauth.provider.info;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.user.model.Role;
import jjun.server.springsecurityjwt.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OAuth2Attributes {

    private String attributeKey;  // OAuth2 로그인 진행 시 키가 되는 필드값(= PK와 동일한 의미)
    private OAuth2UserInfo oAuth2UserInfo;  // 소셜 타입별 로그인 유저 정보 - 닉네이, 이메일, 프로필사진 등

    /**
     * SocialType에 맞는 메서드를 호출하여 OAuthAttributes 객체 반환
     * - 파라미터: userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값
     * - attributes: OAuth 서비스의 유저 정보들
     *
     * 소셜 별 of 메서드(ofGoogle, ofKakao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후에 build한다.
     */
    @Builder
    public static OAuth2Attributes of(SocialPlatform socialPlatform, String userNameAttributeName, Map<String, Object> attributes) {

        if (socialPlatform.equals(SocialPlatform.KAKAO)) {
            return ofKakao(userNameAttributeName, attributes);
        }

        return ofApple(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofKakao(String attributeKey, Map<String, Object> attributes) {
        return new OAuth2Attributes(attributeKey, new KakaoOAuth2UserInfo(attributes));
    }

    private static OAuth2Attributes ofApple(String attributeKey, Map<String, Object> attributes) {
        return new OAuth2Attributes(attributeKey, new AppleOAuth2UserInfo(attributes));
    }

    /**
     * OAuth2Attribute 객체 생성 이후, OAuth2UserInfo에서 socialId(식별값), nickname, email을 가져와서
     * User로 build (role은 GUEST로 설정)
     */
    public User toUserEntity(SocialPlatform socialPlatform, OAuth2UserInfo oAuth2UserInfo) {
        return User.builder()
                .socialPlatform(socialPlatform)
                .socialId(oAuth2UserInfo.getProviderId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(oAuth2UserInfo.getName())
                .role(Role.GUEST)
                .build();
    }
}
