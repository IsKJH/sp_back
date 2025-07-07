package sp.spb.auth.naver_authentication.service;

public interface NaverAuthenticationService {
    String getLoginLink();

    public String processNaverLogin(String code);
}
