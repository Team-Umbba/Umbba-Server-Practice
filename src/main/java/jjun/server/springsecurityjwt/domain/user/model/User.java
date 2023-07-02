package jjun.server.springsecurityjwt.domain.user.model;

import jjun.server.springsecurityjwt.domain.oauth.provider.SocialPlatform;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

//    @Column(nullable = false)
    private String username;  // TODO Spring Security의 username과 맞춰주기 위해 일부러 넣은 필드

//    @Column(nullable = false)
    private String gender;

//    @Column(nullable = false)
    private int bornYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    private String refreshToken;  // JWT Token

    // 재발급한 Refresh Token으로 업데이트  //TODO SocialUser랑 분리하지 않으면 이 refreshToken이 JWT인지 Kakao에서 발급해준 것인지가 모호하다
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //== 소셜 로그인 관련 ==//

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialPlatform socialPlatform;

    @Column(nullable = false)
    private String socialId;  // 로그인한 소셜 타입의 식별자 값(일반 로그인인 경우는 null)

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;


    // Kakao에서 사용자 정보를 받아오기 위한 메서드 (포함하는 정보로 구성되어야 함)
    public void updateSocialInfo(String socialNickname, String socialProfileImage, String socialAccessToken) {
        this.socialNickname = socialNickname;
        this.socialProfileImage = socialProfileImage;
        this.socialAccessToken = socialAccessToken;
//        this.socialRefreshToken = socialRefreshToken;
    }

    // 유저 최초 가입(소셜로그인) 시 필요한 최소한의 정보
    public User(SocialPlatform socialPlatform, String socialId) {
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
    }

    public static User of(Role role, SocialPlatform socialPlatform, String socialId, String socialNickname, String socialProfileImage, String socialAccessToken) {
        return new User(role, socialPlatform, socialId, socialNickname, socialProfileImage, socialAccessToken);
    }

    public User(Role role, SocialPlatform socialPlatform, String socialId, String socialNickname, String socialProfileImage, String socialAccessToken) {
        this.role = role;
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
        this.socialNickname = socialNickname;
        this.socialProfileImage = socialProfileImage;
        this.socialAccessToken = socialAccessToken;
    }


}
