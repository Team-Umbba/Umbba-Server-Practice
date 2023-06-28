package sopt.org.springsecurityjwt.domain.user.dto.response;

import lombok.*;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.user.model.Authority;
import sopt.org.springsecurityjwt.domain.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Long userId;

    private String nickname;

    private String email;

    private List<Authority> roles = new ArrayList<>();

    private TokenDto token;

    public UserLoginResponseDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }
}