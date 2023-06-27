package jjun.server.springsecurityjwt.domain.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.KakaoAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

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


    /**
     * 직접 HTTP 통신용 템플릿을 단순화하여 일일이 값 넣어서 요청 보내는 방식
     */
    /*
    public KakaoAccessTokenResponse getKakaoAccessToken(String code) {

        RestTemplate rt = new RestTemplate();  // 스프링에서 지원하는 REST 서비스 호출 방식
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        // 카카오 공식 문서에 따라 헤더와 바디 값을 구성
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);  // 인가 코드 요청에서 받은 인가 코드 값 <- 아마 프론트에서 주실 것!

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        log.info("KakaoTokenRequest: {}", kakaoTokenRequest);

        // 카카오로부터 Access Token 수신
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (KakaoAccessTokenResponse)
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            KakaoAccessTokenResponse accessTokenResponse = objectMapper.readValue(response.getBody(), KakaoAccessTokenResponse.class);
            return accessTokenResponse;
        } catch (JsonProcessingException e) {
            log.debug("Kakao Access Token JSON Parsing에 실패했습니다.");
        }
    }
     */
}
