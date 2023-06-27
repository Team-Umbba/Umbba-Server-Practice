package jjun.server.springsecurityjwt.domain.oauth.controller;

import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.KakaoAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Kakao 공식 문서에서 [토큰 받기] 참고
 * - 필요한 필드들을 받아오기 위한 클래스
 */
@FeignClient(name = "kakaoAuthApiClient", url = "https://kauth.kakao.com")
public interface KakaoAuthApiClient {

    /**
     * Access Token을 받아온 이후에, 만료 시간 지정, 재발급 등의 로직을 추가해줄 수 있다.
     * 단순 유저 로그인을 위한 용도로 사용하려면 Access Token 만 있어도 충분하지만,
     * 서비스를 지속적으로 이용하고자 한다면 Refresh Token까지 필요
     */
    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoAccessTokenResponse getOAuth2AccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code
    );
}
