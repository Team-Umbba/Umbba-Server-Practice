package jjun.server.springsecurityjwt.domain.oauth.provider;

import jjun.server.springsecurityjwt.domain.user.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 소셜 로그인 유저에 대한 클래스
 */
public class CustomOAuth2User extends DefaultOAuth2User {

    private Role role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.role = role;
    }
}
