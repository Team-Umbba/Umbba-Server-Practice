package jjun.server.springsecurityjwt.domain.user.service;

import jjun.server.springsecurityjwt.domain.jwt.dto.AccessTokenDto;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.user.controller.dto.UserRegisterDto;
import jjun.server.springsecurityjwt.domain.user.model.Role;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public User register(final UserRegisterDto request) throws Exception {

        if (userRepository.findByEmailAndNickname(
                request.getEmail(), request.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();
        user.passwordEncode(passwordEncoder);
        return userRepository.save(user);

    }

    public AccessTokenDto login(String userId, String password) {

        // 1. Login ID/PW를 기반으로 Authentication 객체 생성
        // 이때 Authentication에서 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        // 2. 실제 검증 (입력한 비밀번호에 대한 유효성 체크)
        // authenticate() 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AccessTokenDto tokenDto = jwtTokenProvider.generateAccessToken(authentication, userId);

        return tokenDto;
    }


}
