package jjun.server.springsecurityjwt.domain.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.response.KakaoUserResponse;
import net.minidev.json.JSONArray;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * 카카오 사용자의 정보를 불러오기 위한 클래스
 * - 받아온 Authorization Token 정보만 보내주면 된다!
 */
@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetMapping(value = "/v2/user/me")
    KakaoUserResponse getUserInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);

    /**
     * 직접 템플릿 구현하는 방식: 카카오 사용자 정보를 불러오기 위해 헤더에 카카오에서 받은 액세스 토큰 값을 넣어주자!
     */
    /*protected String getKakaoUserData(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);

        HttpEntity<JSONArray> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Object> responseData = restTemplate.postForEntity("https://kapi.kakao.com", httpEntity, Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(responseData.getBody(), Map.class).get("id").toString();
    }*/
}
