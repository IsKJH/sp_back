package sp.spb.auth.kakao_authentication.service;

public interface KakaoAuthenticationService {
    String getLoginLink();

    public String processKakaoLogin(String code);
}
