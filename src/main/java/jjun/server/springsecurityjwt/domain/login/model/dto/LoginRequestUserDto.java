package jjun.server.springsecurityjwt.domain.login.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequestUserDto {

    @JsonProperty("email")  // 이메일을 로그인 아이디로 사용
    private String userId;

    private String password;
}
