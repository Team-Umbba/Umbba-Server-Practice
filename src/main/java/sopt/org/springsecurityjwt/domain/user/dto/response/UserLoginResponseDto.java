package sopt.org.springsecurityjwt.domain.user.dto.response;

import lombok.*;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.user.model.SocialPlatform;
import sopt.org.springsecurityjwt.domain.user.model.Authority;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Long userId;

    private String username;

    private String gender;

    private Integer bornYear;

    private List<Authority> roles = new ArrayList<>();

    private TokenDto token;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

    private String socialRefreshToken;
}