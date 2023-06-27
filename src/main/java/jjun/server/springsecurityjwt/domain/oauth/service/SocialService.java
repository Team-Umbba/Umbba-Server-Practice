package jjun.server.springsecurityjwt.domain.oauth.service;


import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;

/**
 * 소셜 서비스마다 서비스를 각각 구현해야 할까?
 * 비슷한 로직도 존재하므로 이를 추상 클래스로 구현하여 필요에 따라 구현하도록 하자
 * -> 각 플랫폼마다 조금씩 다르므로 공통으로 사용하기에는 무리가 있음
 */
public abstract class SocialService {

    public abstract Long login(SocialLoginRequest request);

    public void logout(Long userId) {
        // ...
    }
}
