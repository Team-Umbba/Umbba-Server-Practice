package jjun.server.springsecurityjwt.domain.oauth.provider;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {

    GOOGLE("구글"),
    NAVER("네이버"),
    KAKAO("카카오"),
    APPLE("애플");

    private final String value;
}
