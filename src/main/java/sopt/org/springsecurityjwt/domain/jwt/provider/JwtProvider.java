package sopt.org.springsecurityjwt.domain.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sopt.org.springsecurityjwt.domain.jwt.dto.TokenDto;
import sopt.org.springsecurityjwt.domain.jwt.model.RefreshToken;
import sopt.org.springsecurityjwt.domain.jwt.repository.TokenRepository;
import sopt.org.springsecurityjwt.domain.user.model.User;
import sopt.org.springsecurityjwt.domain.user.repository.UserRepository;
import sopt.org.springsecurityjwt.domain.user.security.JpaUserDetailsService;
import sopt.org.springsecurityjwt.domain.user.model.Authority;
import sopt.org.springsecurityjwt.exception.Error;
import sopt.org.springsecurityjwt.exception.model.RefreshExpirationException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String salt;
    private Key secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 1000L;  // 액세스 토큰 만료 시간: 1분으로 지정
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 120 * 1000L;  // 리프레시 토큰 만료 시간: 2분으로 지정

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JpaUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    // Access 토큰 생성
    public String createAccessToken(String email, List<Authority> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh 토큰 생성
    /**
     * Redis 내부에는
     * refreshToken:userId : tokenValue
     * 형태로 저장한다.
     */
    public String createRefreshToken(User user) {
        RefreshToken refreshToken = tokenRepository.save(
                RefreshToken.builder()
                        .id(user.getId())
                        .refreshToken(UUID.randomUUID().toString())
                        .expiration((int) REFRESH_TOKEN_EXPIRE_TIME / 1000)
                        .build()
        );
        return refreshToken.getRefreshToken();
    }

    // Access 토큰 검증
    public boolean validateAccessToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());

        } catch (Exception e) {
            // 토큰이 만료되었을시 false 반환
            return false;
        }
    }

    // Refresh 토큰 검증
    public boolean validRefreshToken(User user, String refreshToken) throws Exception {
        try {
            RefreshToken token = tokenRepository.findById(user.getId()).orElseThrow(Exception::new);

            // 해당유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
            if (token.getRefreshToken() == null) {
                return false;
            } else if (!token.getRefreshToken().equals(refreshToken)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Access 토큰 재발급
    public TokenDto refreshAccessToken(TokenDto token) throws Exception {
        String email = getEmail(token.getAccessToken());
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (validRefreshToken(user, token.getRefreshToken())) {
            return TokenDto.builder()
                    .accessToken(createAccessToken(email, user.getRoles()))
                    .refreshToken(token.getRefreshToken())
                    .build();
        } else {
            //🔥Refresh Token 만료
            throw new RefreshExpirationException(Error.TOKEN_TIME_EXPIRED_EXCEPTION, "다시 로그인 해주세요");
        }
    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에 담겨있는 유저 email 획득
    public String getEmail(String token) {
        // 만료된 토큰에 대해 parseClaimsJws를 수행하면 io.jsonwebtoken.ExpiredJwtException이 발생한다.
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return e.getClaims().getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Authorization Header를 통해 인증을 한다.
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
