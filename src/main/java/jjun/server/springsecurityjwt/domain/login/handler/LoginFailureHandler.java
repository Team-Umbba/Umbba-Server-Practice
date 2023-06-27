package jjun.server.springsecurityjwt.domain.login.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 로그인 실패 처리 핸들러
 * - SimpleUrlAuthenticationFailureHandler 상속
 *
 * 입력된 아이디가 없는 경우나 비밀번호가 틀린 경우 등
 * 로그인 실패 케이스에 따라 핸들러가 동작하도록 한다.
 * AuthenticationException: 로그인 실패 시 예외에 대한 정보를 가지고 있는 클래스
 */
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    // request의 정보를 가지고 response에 대한 설정을 할 수 있다.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("로그인 실패! 이메일이나 비밀번호를 확인해주세요.");
        log.info("JWT 로그인 실패(실패 핸들러 동작) | Message : {}", exception.getMessage());
    }
}
