package jjun.server.springsecurityjwt.domain.oauth.controller.dto.response;

import jjun.server.springsecurityjwt.domain.jwt.dto.TokenDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import jjun.server.springsecurityjwt.domain.user.model.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginResponseDto {

    private Long userId;

    private String username;

    private String gender;

    private int bornYear;

    private TokenDto tokenDto;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

    public static UserLoginResponseDto of(User loginUser, TokenDto tokenDto) {

        return UserLoginResponseDto.builder()
                .tokenDto(tokenDto)
                .userId(loginUser.getId())
                .username(loginUser.getUsername())
                .gender(loginUser.getGender())
                .bornYear(loginUser.getBornYear())
                .tokenDto(tokenDto)
                .socialPlatform(loginUser.getSocialPlatform())
                .socialNickname(loginUser.getSocialNickname())
                .socialProfileImage(loginUser.getSocialProfileImage())
                .socialAccessToken(loginUser.getSocialAccessToken())
                .build();
    }
}
