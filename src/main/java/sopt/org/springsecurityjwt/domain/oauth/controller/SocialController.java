package sopt.org.springsecurityjwt.domain.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sopt.org.springsecurityjwt.domain.oauth.dto.request.SocialLoginRequestDto;
import sopt.org.springsecurityjwt.domain.oauth.service.SocialService;
import sopt.org.springsecurityjwt.domain.oauth.service.SocialServiceProvider;
import sopt.org.springsecurityjwt.exception.dto.ApiResponse;
import sopt.org.springsecurityjwt.exception.Success;
import sopt.org.springsecurityjwt.domain.oauth.dto.request.SocialLoginRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialController {

    private final SocialServiceProvider socialServiceProvider;

    @PostMapping("/login")
    public ApiResponse<Long> login(@RequestHeader("code") String code, @RequestBody SocialLoginRequestDto request) {
        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return ApiResponse.success(Success.SOCIAL_LOGIN_SUCCESS, socialService.login(SocialLoginRequest.of(code)));
    }
}