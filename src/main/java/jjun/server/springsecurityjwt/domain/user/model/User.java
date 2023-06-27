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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;  // TODO Spring Security의 username과 맞춰주기 위해 일부러 넣은 필드


    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 소셜로그인에서 받아오는 토큰
//    @Column(nullable = false)
    private String accessToken;

//    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialPlatform socialPlatform;

    @Column(nullable = false)
    private String socialId;  // 로그인한 소셜 타입의 식별자 값(일반 로그인인 경우는 null)


    // 비밀번호 암호화
    /* public void passwordEncode(BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
*/
    // 재발급한 Refresh Token으로 업데이트  //TODO SocialUser랑 분리하지 않으면 이 refreshToken이 JWT인지 Kakao에서 발급해준 것인지가 모호하다
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User(String email, String username, String nickname, String profileImage, String accessToken, String refreshToken, SocialPlatform socialPlatform, String socialId, Role role) {
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
        this.role = role;
    }

    // Kakao에서 사용자 정보를 받아오기 위한 메서드 (포함하는 정보로 구성되어야 함)
    // - 현재 동의항목 구성 : 닉네임, 프로필 사진, 카카오계정(이메일) TODO 연령대도 추가해야 하나?
    public static User of(String email, String username, String nickname, String profileImage, String accessToken, String refreshToken, SocialPlatform socialPlatform, String socialId, Role role) {
        return new User(email, username, nickname, profileImage, accessToken, refreshToken, socialPlatform, socialId, role);
        // TODO 1. username과 같이 소셜에서 받아올 때 없는 정보들은 어떻게 처리할지? (null? or email으로 일단 대체?)
        // TODO 2. 우리는 소셜 유저밖에 없으니까 하나의 유저로 퉁 치고 싶은데 그게 추가적인 필드가 너무 많이 필요하다면 그냥 분리해
    }


}
