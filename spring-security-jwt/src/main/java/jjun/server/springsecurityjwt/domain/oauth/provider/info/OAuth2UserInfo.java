package jjun.server.springsecurityjwt.domain.oauth.provider.info;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;

/**
 * 플랫폼마다 response 형식이 조금씩 다르므로,
 * 각각에 대해 필요한 구조로 정보를 받도록 인터페이스로 구현한다.
 */
public interface OAuth2UserInfo {

    String getProviderId();
    SocialPlatform getProvider();
    String getEmail();
    String getName();
}
