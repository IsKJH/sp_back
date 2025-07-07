package sp.spb.auth.google_authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sp.spb.auth.google_authentication.repository.GoogleAuthenticationRepository;
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
public class GoogleAuthenticationServiceImpl implements GoogleAuthenticationService {

    private final GoogleAuthenticationRepository googleAuthenticationRepository;
    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final JwtUtil jwtUtil;

    @Override
    public String getLoginLink() {
        return googleAuthenticationRepository.getLoginLink();
    }

    @Override
    public String processGoogleLogin(String code) {
        Map<String, Object> tokenResponse = googleAuthenticationRepository.getAccessToken(code);
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = googleAuthenticationRepository.getUserInfo(accessToken);

        String googleId = userInfo.get("id").toString();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        User user = findOrCreateUser(email, name, googleId);

        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }

    private User findOrCreateUser(String email, String name, String googleId) {
        Optional<SocialLogin> existingSocialLogin = socialLoginRepository.findByProviderAndProviderId(SocialProvider.GOOGLE, googleId);

        if (existingSocialLogin.isPresent()) {
            return existingSocialLogin.get().getUser();
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> createNewUser(email, name));

        SocialLogin socialLogin = SocialLogin.builder().user(user).provider(SocialProvider.GOOGLE).providerId(googleId).build();

        socialLoginRepository.save(socialLogin);
        return user;
    }

    private User createNewUser(String email, String name) {
        User user = User.builder().email(email).name(name).build();
        return userRepository.save(user);
    }
}
