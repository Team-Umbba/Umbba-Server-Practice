package jjun.server.springsecurityjwt.domain.jwt.dto;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenReissuedRequestDto {

    @NotNull
    private String accessToken;
    @NotNull
    private String refreshToken;

}
