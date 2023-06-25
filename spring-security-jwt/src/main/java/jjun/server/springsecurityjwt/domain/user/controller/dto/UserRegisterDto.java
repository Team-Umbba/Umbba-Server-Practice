package jjun.server.springsecurityjwt.domain.user.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterDto {

    private String email;
    private String password;
    private String nickname;
}
