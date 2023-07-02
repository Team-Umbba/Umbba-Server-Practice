package jjun.server.springsecurityjwt.domain.jwt.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public class TokenDto {

    private String accessToken;
    private String refreshToken;
}
