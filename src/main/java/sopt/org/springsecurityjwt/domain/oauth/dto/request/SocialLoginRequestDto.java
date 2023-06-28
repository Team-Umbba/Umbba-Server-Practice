package sopt.org.springsecurityjwt.domain.oauth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.springsecurityjwt.domain.oauth.model.SocialPlatform;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialLoginRequestDto {

    private SocialPlatform socialPlatform;
}
