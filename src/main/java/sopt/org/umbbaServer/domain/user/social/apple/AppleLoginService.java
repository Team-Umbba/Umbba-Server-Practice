package sopt.org.umbbaServer.domain.user.social.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppleLoginService {

    @Value("${apple.url}")
    private String APPLE_URL;


}