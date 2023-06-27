package jjun.server.springsecurityjwt.domain.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${jwt.access.header}")
    private String accessTokenHeader;
    @Value("${jwt.refresh.header}")
    private String refreshTokenHeader;
    @Value("${jwt.secret}")
    private String secretKey;

    // Access Token 을 헤더에 실어서 보내는 경우
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessTokenHeader, accessToken);
        log.info("재발급된 Access Token: {}", accessToken);
    }

    // Access Token + Refresh Token 을 헤더에 실어서 보내는 경우
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }


    /**
     * 헤더에서 Refresh Token 추출
     * 토큰 형식: "Bearer XXX" 에서 prefix인 "Bearer"를 제외한 순수 토큰만 가져오기
     * TODO 로직 변경의 여지가 있다!
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(
                request.getHeader(refreshTokenHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * 헤더에서 Access Token 추출
     * 토큰 형식: "Bearer XXX"에서 prefix인 "Bearer"를 제외하고 순수 토큰만 가져오기
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(
                        request.getHeader(accessTokenHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /**
     * Access Token에서 유저 정보인 email을 추출
     *
     * 1. 추출 전에 JWT.require()로 검증기 생성
     * 2. verify로 Access Token의 유효성 검증 후
     * 3. 유효한 경우에 getClaim()으로 이메일을 추출한다.
     * 4. 유효하지 않다면 빈 Optional 객체를 반환한다.
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()   // 1. 반환된 필터로 JWT verifier 생성
                    .verify(accessToken)  // 2. Access Token 유효성 검사 (유효하지 않다면 예외 발생)
                    .getClaim(EMAIL_CLAIM)  // 3. 검사에 통과 후 이메일 추출
                    .asString());
        } catch (Exception e) {
            log.error("토큰 유효성 검사 실패 | Access Token이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    // Access Token 헤더 설정
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessTokenHeader, accessToken);
    }

    // Refresh Token 헤더 설정
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshTokenHeader, refreshToken);
    }

    // Refresh Token DB에 저장 TODO Redis에 저장하도록 수정 필요
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );

    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
