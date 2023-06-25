package jjun.server.springsecurityjwt.domain.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jjun.server.springsecurityjwt.domain.jwt.dto.AccessTokenDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component  // 컴포넌트로 등록했으므로 자동으로 IoC가 된다.
@Slf4j
public class JwtTokenProvider {

    private final Key key;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationPeriod;
    @Value("${jwt.access.subject}")
    private String accessTokenSubject;
    @Value("${jwt.refresh.subject}")
    private String refreshTokenSubject;

    /**
     * Subject -> Authentication Name
     */

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);  // HMAC & SHA 알고리즘으로 키 생성
    }

    // Token을 이용해 유저의 권한(Role)을 가져오는 메소드
    private String getAuthenticationAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // Refresh Token 발급 -> Dto 객체에 담아서 반환
    public RefreshTokenDto generateRefreshToken() {

        Date refreshTokenExpiresIn = new Date(new Date().getTime() + refreshTokenExpirationPeriod);  // 만료 시간 지정

        String refreshToken = Jwts.builder()
                .setSubject(refreshTokenSubject)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return RefreshTokenDto.builder()
                .grantType("Bearer")
                .refreshToken(refreshToken)
                .build();
    }

    // Access Token 발급 (유저 정보를 claim에 담기)
    public AccessTokenDto generateAccessToken(Authentication authentication) {

        Date accessTokenExpiresIn = new Date(new Date().getTime() + accessTokenExpirationPeriod);  // 만료 시간 지정

        String accessToken = Jwts.builder()
                .setSubject(accessTokenSubject)
                // 권한 가져오기
                .claim("auth", getAuthenticationAuthority(authentication))
                //.claim("email") TODO 유저 정보 클레임에 넣기 (세미나 실습 참고)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return AccessTokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    // JWT 토큰 복호확하여 토큰에 들어있는 정보를 가져오는 메서드
    public Authentication getAuthentication(String accessToken) {

        // 토큰 복호화
        Claims claims = parseClaim(accessToken);  // TODO 심야식당에서 뜯어본 코드 탐고

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        /**
         * 클레임에서 권한 정보 가져오기
         *
         * SimpleGrantedAuthority란?
         * Granted Authority 를 implement 한 클래스
         *
         * - Spring Security에서 기본적으로 ROLE_USER, ROLE_ADMIN, ROLE_GUEST의 권한을 구분하여 제공한다.
         * - 권한을 string으로 변환해주기
         */
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaim(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {  // 토큰 만료 시 이에 대한 클레임을 반환한다.
            return e.getClaims();
        }
    }

    // 토큰 정보 검증 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

}
