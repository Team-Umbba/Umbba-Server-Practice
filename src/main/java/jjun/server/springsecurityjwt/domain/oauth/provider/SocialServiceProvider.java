package jjun.server.springsecurityjwt.domain.oauth.provider;

import jjun.server.springsecurityjwt.domain.oauth.service.KakaoSocialService;
import jjun.server.springsecurityjwt.domain.oauth.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 *  Controller 단에서 어떻게 소셜로그인의 종류를 구분하고 각각에 대한 로그인 처리를할 수 있을까?
 *  API 하나로 만드는 것은 비효율적!
 *  클라이언트가 어떤 플랫폼인지를 요청에 함께 보내주면 이 정보를 이용해 구분하도록 Provider 클래스를 하나 더 생성
 *
 *
 * OAuth2Attributes와 같이 공통 인터페이스로 구현하는 방법은 플랫폼마다 디테일적인 부분이 많이 달라 한계가 있다.
 * 그냥 코드가 비록 더럽더라도 일일이 분리해서 구현해주는 것이 편하다.
 * 해당 클래스는 분리를 하고 이를 Map 자료구조로 관리하여 타입을 구분하는 방식으로 구현한 것이다.
 */
@Component
@RequiredArgsConstructor
public class SocialServiceProvider {

    // 구현한 소셜 서비스를 Map 에 모두 추가해준다. -> 클라이언트에서 받은 플랫폼 정보를 key 값으로 저장
    private static final Map<SocialPlatform, SocialService> socialServiceMap = new HashMap<>();

    private final KakaoSocialService kakaoSocialService;

    @PostConstruct
    void initializeSocialServiceMap() {
        socialServiceMap.put(SocialPlatform.KAKAO, kakaoSocialService);
    }

    public SocialService getSocialService(SocialPlatform socialPlatform) {
        return socialServiceMap.get(socialPlatform);
    }

}
