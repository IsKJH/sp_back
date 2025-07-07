package sp.spb.auth.naver_authentication.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
public class NaverAuthenticationRepositoryImpl implements NaverAuthenticationRepository {
    private final String loginUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenRequestUri;
    private final String userInfoRequestUri;
    private final RestTemplate restTemplate = new RestTemplate();

    public NaverAuthenticationRepositoryImpl(
            @Value("${naver.login-url}") String loginUrl,
            @Value("${naver.client-id}") String clientId,
            @Value("${naver.client-secret}") String clientSecret,
            @Value("${naver.redirect-uri}") String redirectUri,
            @Value("${naver.token-request-uri}") String tokenRequestUri,
            @Value("${naver.user-info-request-uri}") String userInfoRequestUri
    ) {
        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenRequestUri = tokenRequestUri;
        this.userInfoRequestUri = userInfoRequestUri;
    }

    @Override
    public String getLoginLink() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
                loginUrl, clientId, redirectUri);
    }

    @Override
    public Map<String, Object> getAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);
        formData.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenRequestUri, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }

    @Override
    public Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoRequestUri, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
