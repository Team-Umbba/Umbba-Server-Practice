package sopt.org.springsecurityjwt.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.springsecurityjwt.domain.jwt.provider.JwtProvider;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.jwt.repository.TokenRepository;
import sopt.org.springsecurityjwt.domain.user.dto.request.UserLoginRequestDto;
import sopt.org.springsecurityjwt.domain.user.dto.response.UserLoginResponseDto;
import sopt.org.springsecurityjwt.domain.user.dto.request.UserSignupRequestDto;
import sopt.org.springsecurityjwt.domain.user.model.Authority;
import sopt.org.springsecurityjwt.domain.user.model.User;
import sopt.org.springsecurityjwt.domain.user.repository.UserRepository;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserLoginResponseDto login(UserLoginRequestDto request) throws Exception {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        //Refresh 토큰 재발급
        user.setRefreshToken(jwtProvider.createRefreshToken(user));

        return UserLoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .roles(user.getRoles())
                .token(TokenDto.builder()
                                .accessToken(jwtProvider.createAccessToken(user.getEmail(), user.getRoles()))
                                .refreshToken(user.getRefreshToken()).build())
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
}
