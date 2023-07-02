package jjun.server.springsecurityjwt.domain.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jjun.server.springsecurityjwt.domain.jwt.dto.*;
import jjun.server.springsecurityjwt.domain.jwt.filter.UserAuthentication;
import jjun.server.springsecurityjwt.domain.jwt.model.RefreshToken;
import jjun.server.springsecurityjwt.domain.jwt.model.RefreshTokenRepository;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.model.CustomException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component  // 컴포넌트로 등록했으므로 자동으로 IoC가 된다.
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.subject}")
    private String accessTokenSubject;
    @Value("${jwt.refresh.subject}")
    private String refreshTokenSubject;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 120 * 60 *1000L;  // 액세스토큰 만료 시간: 2시간으로 지정
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 *1000L;  // 리프레시토큰 만료 시간: 1시간으로 지정

    private static final String BEARER_TYPE = "Bearer ";
    private static final String AUTHORITIES_KEY = "auth";

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Subject -> Authentication Name
     */

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder()
                .encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));  // JWT 인코딩은 호출 시점에 딱 한번만 처리되어야 한다.
    }

    // Token을 이용해 유저의 권한(Role)을 가져오는 메소드
    private String getAuthenticationAuthority(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // JWT Token (Access + Refresh) 발급
    public TokenDto issueToken(Long userId) {

        UserAuthentication authentication = new UserAuthentication(userId, null, null);

        return TokenDto.builder()
                .accessToken(generateAccessToken(authentication))
                .refreshToken(generateRefreshToken(userId))
                .build();
    }

    // Refresh Token 발급 -> Dto 객체에 담아서 반환
    public String generateRefreshToken(Long userId) {

        final Date now = new Date();
        Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);  // 만료 시간 지정

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiresIn);


        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(refreshTokenSubject)
                .setClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(userId)
                .expiration(Integer.parseInt(String.valueOf(REFRESH_TOKEN_EXPIRE_TIME)) / 1000)
                .build());

        return refreshToken;
    }

    // Access Token 발급 (유저 정보를 claim에 담기)
    public String generateAccessToken(Authentication authentication) {

        final Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);  // 만료 시간 지정

        // 클레임 생성 -> Payload : 등록된 클레임 중 전부를 담아야 하는 것 X (필요에 따라 선택적으로 담아주면 된다)
        final Claims claims = Jwts.claims()
                .setSubject(accessTokenSubject)  // token 이름(고정된 값X, 어떤 값인지 구분하기 위함)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn);  // milliseconds 단위 (*1000 = 1초) => 만료시간 2시간으로 지정


        // private claim 등록
        claims.put("userId", authentication.getPrincipal());   // 유저 정보
        claims.put(AUTHORITIES_KEY, getAuthenticationAuthority(authentication));  // 권한 가져오가

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAccessToken(Long userId) {

        final Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);  // 만료 시간 지정

        // 클레임 생성 -> Payload : 등록된 클레임 중 전부를 담아야 하는 것 X (필요에 따라 선택적으로 담아주면 된다)
        final Claims claims = Jwts.claims()
                .setSubject(accessTokenSubject)  // token 이름(고정된 값X, 어떤 값인지 구분하기 위함)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn);  // milliseconds 단위 (*1000 = 1초) => 만료시간 2시간으로 지정


        // private claim 등록
        claims.put("userId", userId);   // 유저 정보

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    // JWT 토큰 복호확하여 토큰에 들어있는 정보를 가져오는 메서드
    public Authentication getAuthentication(String accessToken) {

        // 토큰 복호화
        Claims claims = parseClaim(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
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
            return Jwts.parserBuilder().setSigningKey(getSigningKey())
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
            final Claims claims = getBody(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {  // TODO Custom 예외 던지기
            log.info("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);  // TODO 토큰 reissue 로직 추가
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰값이 존재하지 않습니다.", e);
        }
        return false;
    }

    // Refresh Token의 유효성 검사 후 재발급 - 1. Redis에 저장된 토큰과의 일치 여부  2. 토큰 만료 여부
    public TokenReissuedResponseDto reissueToken(TokenReissuedRequestDto request) {

        final RefreshToken tokenFromRedis = refreshTokenRepository.findById(request.getRefreshToken()).orElseThrow(
                () -> new CustomException(Error.INVALID_REFRESH_TOKEN)
        );

        if (!tokenFromRedis.getRefreshToken().equals(request.getRefreshToken())) {
            throw new CustomException(Error.NO_MATCH_REFRESH_TOKEN);
        }

        // TODO 재발급을 Access + Refresh 모두 처리해줘도 되는지?
        // Refresh Token의 유효성 검사를 통과하면 재발급 진행
        final String reissuedAccessToken = generateAccessToken(tokenFromRedis.getUserId());
        final TokenDto reissuedToken = TokenDto.builder()
                .accessToken(reissuedAccessToken)   // 얘는 새로 발급
                .refreshToken(request.getRefreshToken())  // 얘는 기존
                .build();
        // TODO 문제점 - Redis에 있는 토큰의 업데이트가 안됨 -> 위처럼 해주면 업데이트 할 필요 X
        return TokenReissuedResponseDto.of(reissuedToken.getAccessToken(), reissuedToken.getRefreshToken());
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // JWT 토큰에서 유저 아이디 가져오기
    public Long getUserFromJwt(String token) {
        final Claims claims = getBody(token);
        return (Long) claims.get("userId");
    }

}
