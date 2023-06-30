package sopt.org.springsecurityjwt.domain.user.service;

import sopt.org.springsecurityjwt.domain.user.dto.request.SocialLoginRequest;
import sopt.org.springsecurityjwt.domain.user.dto.response.UserLoginResponseDto;

public abstract class SocialService {
    public abstract UserLoginResponseDto login(SocialLoginRequest request);

    public void logout(Long userId) {
        // 아쩌고 저쩌고 로직
    }
}