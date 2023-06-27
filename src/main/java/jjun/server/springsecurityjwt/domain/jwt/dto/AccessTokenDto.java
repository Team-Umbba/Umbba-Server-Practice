package jjun.server.springsecurityjwt.domain.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class AccessTokenDto {

    /**
     * grantType: jwt에 대한 인증 타입
     * - 여기서는 "bearer"를 사용한다.
     */
    private String grantType;
    private String accessToken;
}
