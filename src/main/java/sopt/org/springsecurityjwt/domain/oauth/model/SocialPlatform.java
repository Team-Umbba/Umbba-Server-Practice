package sopt.org.springsecurityjwt.domain.oauth.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {
    KAKAO("카카오"),
    GOOGLE("구글"),
    APPLE("애플")
    ;

    private final String value;
}