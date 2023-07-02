package jjun.server.springsecurityjwt.domain.oauth.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

/**
 * KakaoUser -> KakaoAccount -> KakaoProfile
 * 로 들어가야 실질적으로 우리가 서비스에 사용할 사용자 정보를 가져올 수 있다.
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserResponse {

    private Long id;
    private KakaoAccount kakaoAccount;  // user -> account 정보로 연결하여 가져오기
}
