package sopt.org.springsecurityjwt.service;

import sopt.org.springsecurityjwt.service.dto.request.SocialLoginRequest;

public abstract class SocialService {
    public abstract Long login(SocialLoginRequest request);

    public void logout(Long userId) {
        // 아쩌고 저쩌고 로직
    }
}