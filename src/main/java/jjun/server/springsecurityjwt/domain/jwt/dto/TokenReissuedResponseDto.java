package jjun.server.springsecurityjwt.domain.jwt.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenReissuedResponseDto {

    private String accessToken;
    private String refreshToken;

    public static TokenReissuedResponseDto of(String accessToken, String refreshToken) {
        return new TokenReissuedResponseDto(accessToken, refreshToken);
    }
}
