package jjun.server.springsecurityjwt.domain.login.handler;

import jjun.server.springsecurityjwt.domain.jwt.dto.AccessTokenDto;
import jjun.server.springsecurityjwt.domain.jwt.dto.RefreshTokenDto;
import jjun.server.springsecurityjwt.domain.jwt.provider.JwtTokenProvider;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 로그인 성공 처리 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String email = authentication.getPrincipal().toString();
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken((Long) authentication.getPrincipal());  // TODO 발급한 Refresh Token은 Redis에 유저 아이디와 함께 저장한다.
            response.setStatus(HttpServletResponse.SC_OK);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

//        userRepository.findByEmailAndNickname(email, );
    }

    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");  // 헤더에서 토큰값을 가져온다.

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(6);
        }
        return null;
    }
}
