package sp.spb.auth.kakao_authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sp.spb.auth.kakao_authentication.repository.KakaoAuthenticationRepository;
import sp.spb.auth.user.entity.SocialLogin;
import sp.spb.auth.user.entity.SocialProvider;
import sp.spb.auth.user.entity.User;
import sp.spb.auth.user.repository.SocialLoginRepository;
import sp.spb.auth.user.repository.UserRepository;
import sp.spb.util.JwtUtil;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    private final KakaoAuthenticationRepository kakaoAuthenticationRepository;
    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final JwtUtil jwtUtil;

    @Override
    public String getLoginLink() {
        return kakaoAuthenticationRepository.getLoginLink();
    }

    @Override
    public String processKakaoLogin(String code) {
        Map<String, Object> tokenResponse = kakaoAuthenticationRepository.getAccessToken(code);
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = kakaoAuthenticationRepository.getUserInfo(accessToken);

        String kakaoId = userInfo.get("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");

        User user = findOrCreateUser(email, name, kakaoId);

        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }

    private User findOrCreateUser(String email, String name, String kakaoId) {
        Optional<SocialLogin> existingSocialLogin = socialLoginRepository
                .findByProviderAndProviderId(SocialProvider.KAKAO, kakaoId);

        if (existingSocialLogin.isPresent()) {
            return existingSocialLogin.get().getUser();
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name));

        SocialLogin socialLogin = SocialLogin.builder()
                .user(user)
                .provider(SocialProvider.KAKAO)
                .providerId(kakaoId)
                .build();

        socialLoginRepository.save(socialLogin);
        return user;
    }

    private User createNewUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return userRepository.save(user);
    }

}
