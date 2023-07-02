package jjun.server.springsecurityjwt.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Success {

    /**
     * 200 OK
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    SOCIAL_LOGIN_SUCCESS(HttpStatus.OK, "소셜 로그인에 성공했습니다."),
    AUTHORIZATION_SUCCESS(HttpStatus.OK, "토큰 재인증에 성공했습니다"),
    KAKAO_ACCESS_TOKEN_SUCCESS(HttpStatus.OK, "카카오 엑세스 토큰을 가져오는데 성공했습니다"),



    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
