package sopt.org.springsecurityjwt.domain.user.dto.response;

import lombok.*;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.user.model.SocialPlatform;
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

    public static UserLoginResponseDto of(User loginUser, String accessToken) {
        TokenDto token = TokenDto.builder().accessToken(accessToken).refreshToken(loginUser.getRefreshToken()).build();

        return new UserLoginResponseDto(
                loginUser.getId(), loginUser.getUsername(), loginUser.getGender(), loginUser.getBornYear(),
                loginUser.getRoles(), token,
                loginUser.getSocialPlatform(), loginUser.getSocialNickname(), loginUser.getSocialProfileImage(),
                loginUser.getSocialAccessToken(), loginUser.getSocialRefreshToken());
    }
}