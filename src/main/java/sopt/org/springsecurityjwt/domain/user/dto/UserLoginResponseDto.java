package sopt.org.springsecurityjwt.domain.user.dto;

import lombok.*;
import sopt.org.springsecurityjwt.domain.user.jwt.TokenDto;
import sopt.org.springsecurityjwt.domain.user.social.SocialPlatform;
import sopt.org.springsecurityjwt.domain.user.model.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Long userId;

    private String username;

    private String gender;

    private Integer bornYear;

    private TokenDto tokenDto;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;

    public static UserLoginResponseDto of(User loginUser, String accessToken) {
        TokenDto tokenDto = TokenDto.of(accessToken, loginUser.getRefreshToken());

        return new UserLoginResponseDto(
                loginUser.getId(), loginUser.getUsername(), loginUser.getGender(), loginUser.getBornYear(),
                tokenDto,
                loginUser.getSocialPlatform(), loginUser.getSocialNickname(), loginUser.getSocialProfileImage(),
                loginUser.getSocialAccessToken()/*, loginUser.getSocialRefreshToken()*/);
    }
}