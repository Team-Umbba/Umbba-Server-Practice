package jjun.server.springsecurityjwt.domain.user.service;

import jjun.server.springsecurityjwt.domain.user.controller.dto.UserRegisterDto;
import jjun.server.springsecurityjwt.domain.user.model.Role;
import jjun.server.springsecurityjwt.domain.user.model.User;
import jjun.server.springsecurityjwt.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User register(final UserRegisterDto request) throws Exception {

        if (userRepository.findByEmailAndNickname(
                request.getEmail(), request.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();
        user.passwordEncode(passwordEncoder);
        return userRepository.save(user);

    }
}
