package jjun.server.springsecurityjwt.domain.oauth.controller.dto.request;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialLoginRequestDto {

    private SocialPlatform socialPlatform;

}
