package sopt.org.springsecurityjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.config.jwt.JwtProvider;
import sopt.org.springsecurityjwt.config.jwt.Token;
import sopt.org.springsecurityjwt.config.jwt.TokenDto;
import sopt.org.springsecurityjwt.config.jwt.TokenRepository;
import sopt.org.springsecurityjwt.controller.dto.request.UserLoginRequestDto;
import sopt.org.springsecurityjwt.controller.dto.request.UserSignupRequestDto;
import sopt.org.springsecurityjwt.controller.dto.response.UserLoginResponseDto;
import sopt.org.springsecurityjwt.domain.Authority;
import sopt.org.springsecurityjwt.domain.User;
import sopt.org.springsecurityjwt.infrastructure.UserRepository;

import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    public UserLoginResponseDto login(UserLoginRequestDto request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        user.setRefreshToken(createRefreshToken(user));

        return UserLoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .token(TokenDto.builder()
                                .access_token(jwtProvider.createToken(user.getEmail(), user.getRoles()))
                                .refresh_token(user.getRefreshToken()).build())
                .build();
    }

    public boolean signup(UserSignupRequestDto request) throws Exception {
        try {
            User user = User.builder()
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            user.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            userRepository.save(user);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
        return true;
    }

    public UserLoginResponseDto getUser(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("계정을 찾을 수 없습니다."));
        return new UserLoginResponseDto(user);
    }

    // Refresh Token ================

    /**
     * Refresh 토큰을 생성한다.
     * Redis 내부에는
     * refreshToken:userId : tokenValue
     * 형태로 저장한다.
     */
    public String createRefreshToken(User user) {
        Token token = tokenRepository.save(
                Token.builder()
                        .id(user.getId())
                        .refresh_token(UUID.randomUUID().toString())
                        .expiration(300)
                        .build()
        );
        return token.getRefresh_token();
    }

    public Token validRefreshToken(User user, String refreshToken) throws Exception {
        Token token = tokenRepository.findById(user.getId()).orElseThrow(() -> new Exception("만료된 계정입니다. 로그인을 다시 시도하세요"));

        // 해당유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
        if (token.getRefresh_token() == null) {
            return null;
        } else if (!token.getRefresh_token().equals(refreshToken)) {
            return null;
        } else {
            return token;
        }
    }

    public TokenDto refreshAccessToken(TokenDto token) throws Exception {
        String email = jwtProvider.getEmail(token.getAccess_token());
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));
        Token refreshToken = validRefreshToken(user, token.getRefresh_token());

        if (refreshToken != null) {
            return TokenDto.builder()
                    .access_token(jwtProvider.createToken(email, user.getRoles()))
                    .refresh_token(refreshToken.getRefresh_token())
                    .build();
        } else {
            throw new Exception("로그인을 해주세요");
        }
    }
}
