package sp.spb.auth.kakao_authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sp.spb.auth.kakao_authentication.repository.KakaoAuthenticationRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {
    final private KakaoAuthenticationRepository kakaoAuthenticationRepository;

    @Override
    public String getLoginLink() {
        return this.kakaoAuthenticationRepository.getLoginLink();
    }

    @Override
    public Map<String, Object> requestAccessToken(String code) {
        return this.kakaoAuthenticationRepository.getAccessToken(code);
    }

    @Override
    public Map<String, Object> requestUserInfo(String accessToken) {
        return this.kakaoAuthenticationRepository.getUserInfo(accessToken);
    }
}
