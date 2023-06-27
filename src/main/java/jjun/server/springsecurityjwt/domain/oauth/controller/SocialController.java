package jjun.server.springsecurityjwt.domain.oauth.controller;

import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequest;
import jjun.server.springsecurityjwt.domain.oauth.controller.dto.request.SocialLoginRequestDto;
import jjun.server.springsecurityjwt.domain.oauth.provider.SocialServiceProvider;
import jjun.server.springsecurityjwt.domain.oauth.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Authorization의 기본은 Header에 정보를 담아서 요청을 보내는 것을 권장!
 * => code를 헤더에 실어서 보내도록 구현하자 (이 code는 단발성이기 떄문에 한 번 잘못되면 다시 발급받도록 해야 함)
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/social")
public class SocialController {

    private final SocialServiceProvider socialServiceProvider;

    @PostMapping("/login")
    public Long login(@RequestHeader("code") String code, @RequestBody SocialLoginRequestDto request) {

        // TODO 이미 가입된 유저인지 확인하는 절차 추가
        SocialService socialService = socialServiceProvider.getSocialService(request.getSocialPlatform());
        return socialService.login(SocialLoginRequest.of(code));
    }




}
