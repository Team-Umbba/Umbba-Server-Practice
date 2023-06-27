package jjun.server.springsecurityjwt.domain.oauth.provider.info;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class AppleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return (String) attributes.get("id_token");
    }

    @Override
    public SocialPlatform getProvider() {
        return SocialPlatform.APPLE;
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

        if (account == null) {
            return null;
        }
        return (String) account.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        if (account.isEmpty() || profile.isEmpty()) {
            return null;
        }
        return (String) profile.get("nickname");
    }
}
