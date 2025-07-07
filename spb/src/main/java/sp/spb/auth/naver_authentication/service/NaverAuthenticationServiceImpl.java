package sp.spb.auth.naver_authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sp.spb.auth.naver_authentication.repository.NaverAuthenticationRepository;
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
public class NaverAuthenticationServiceImpl implements NaverAuthenticationService {
    private final NaverAuthenticationRepository naverAuthenticationRepository;
    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final JwtUtil jwtUtil;

    @Override
    public String getLoginLink() {
        return naverAuthenticationRepository.getLoginLink();
    }

    @Override
    public String processNaverLogin(String code) {
        Map<String, Object> tokenResponse = naverAuthenticationRepository.getAccessToken(code);
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = naverAuthenticationRepository.getUserInfo(accessToken);

        Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
        String naverId = response.get("id").toString();
        String email = (String) response.get("email");
        String name = (String) response.get("name");

        User user = findOrCreateUser(email, name, naverId);

        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }

    private User findOrCreateUser(String email, String name, String naverId) {
        Optional<SocialLogin> existingSocialLogin = socialLoginRepository.findByProviderAndProviderId(SocialProvider.NAVER, naverId);

        if (existingSocialLogin.isPresent()) {
            return existingSocialLogin.get().getUser();
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> createNewUser(email, name));

        SocialLogin socialLogin = SocialLogin.builder().user(user).provider(SocialProvider.NAVER).providerId(naverId).build();

        socialLoginRepository.save(socialLogin);
        return user;
    }

    private User createNewUser(String email, String name) {
        User user = User.builder().email(email).name(name).build();
        return userRepository.save(user);
    }
}
