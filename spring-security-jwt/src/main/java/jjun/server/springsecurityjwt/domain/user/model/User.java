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

    @Column
    private String email;

    @Column
    private String username;  // TODO Spring Security의 username과 맞춰주기 위해 일부러 넣은 필드

    @Column
    private String password;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column
    private SocialPlatform socialPlatform;

    private String socialId;  // 로그인한 소셜 타입의 식별자 값(일반 로그인인 경우는 null)


    // 비밀번호 암호화
    public void passwordEncode(BCryptPasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 재발급한 Refresh Token으로 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
