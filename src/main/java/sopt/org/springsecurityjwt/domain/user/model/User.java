package sopt.org.springsecurityjwt.domain.user.model;

import lombok.*;
import sopt.org.springsecurityjwt.domain.common.AuditingTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false) // 사실 온보딩 단계에서 입력되기 때문에 nullable = true로 가져가야함
    private String username;

//    @Column(nullable = false)
    private String gender;

//    @Column(nullable = false)
    private Integer bornYear;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    public void setRoles(List<Authority> role) {
        this.roles = role;
        role.forEach(o -> o.setUser(this));
    }

    private String refreshToken;

    public void setRefreshToken(String refreshToken) { // 추가!
        this.refreshToken = refreshToken;
    }

    // ** 소셜 로그인 관련 **
    @Enumerated(EnumType.STRING)
    private SocialPlatform socialPlatform;

    @Column(nullable = false) // 이걸 PK로 가져갈지 고민
    private Long socialId;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

    private String socialRefreshToken;
    //

    // 로그인 새롭게 할 때마다 해당 필드들 업데이트
    public void updateSocialInfo(String socialNickname, String socialProfileImage, String socialAccessToken, String socialRefreshToken) {
        this.socialNickname = socialNickname;
        this.socialProfileImage = socialProfileImage;
        this.socialAccessToken = socialAccessToken;
        this.socialRefreshToken = socialRefreshToken;
    }

    public User(SocialPlatform socialPlatform, Long socialId) {
        this.socialPlatform = socialPlatform;
        this.socialId = socialId;
    }
}
