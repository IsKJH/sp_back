package sp.spb.auth.naver_authentication.repository;

import java.util.Map;

public interface NaverAuthenticationRepository {
    String getLoginLink();

    Map<String, Object> getAccessToken(String code);

    Map<String, Object> getUserInfo(String accessToken);
}
