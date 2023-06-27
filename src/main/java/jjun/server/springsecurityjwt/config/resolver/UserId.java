package jjun.server.springsecurityjwt.config.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 유저 인증 어노테이션
 * -> Controller에서 Authorization 헤더값을 꺼내와 UserRepository에 존재하는 userId와 일치하는지의 동작이 이루어짐
 * 유저 인증의 경우, 대부분의 API에서 필요한 과정이므로 코드의 중복이 많아진다.
 * 이를 해결하기 위해 어노테이션을 만들어 간단하게 유저 인증 과정을 거칠 수 있도록 한다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)  // 어노테이션이 사용될 범위를 지정 *동작은 Resolver로 분리한다.
public @interface UserId {
}