package jjun.server.springsecurityjwt.config;

import jjun.server.springsecurityjwt.domain.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

    /**
     * FilterChainProxy에서 각각의 Filter들이 체인 형식으로 연결되어 수행된다.
     * 사용자가 처음 인증 요청을 보내면, FilterChainProxy에게 요청을 위임하게 되는데, 아래 등록된 Bean을 가장 먼저 찾는 것이다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .formLogin().disable()    // Form Login 사용 X
                .httpBasic().disable()    // httpBasic 사용 X
                .csrf().disable()
                .headers().frameOptions().disable()

                // CSRF 보안 사용 X
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // Filter 추가
                .and()
                .addFilter(corsFilter)

                // URL별 권한 관리 옵션
                .authorizeRequests()
                .antMatchers("/guest/**")
                .access("hasAnyRole('ROLE_USER', 'ROLE_GUEST')")
                .antMatchers("/user/**")
                .access("hasRole('ROLE_USER')")
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**")  // 기본 페이지, css, image, js 하위 폴더의 파일과 h2-console은 누구나 접근 가능
                .permitAll()

                // 소셜 로그인 설정
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(customOAuth2UserService)
        ;

        return http.build();
    }
}
