package sp.spb.auth.kakao_authentication.controller.request_form;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sp.spb.auth.kakao_authentication.service.KakaoAuthenticationService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao-authentication")
public class KakaoAuthenticationController {
    final private KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/request-login-url")
    public String requestGetLoginLink() {
        return kakaoAuthenticationService.getLoginLink();
    }

    @GetMapping("/login")
    @Transactional
    public void requestAccessToken(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

        try {
            Map<String, Object> tokenResponse = kakaoAuthenticationService.requestAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            Map<String, Object> userInfo = kakaoAuthenticationService.requestUserInfo(accessToken);

            String htmlResponse = """
            <html>
              <body>
                <script>
                  window.opener.postMessage({
                    accessToken: '%s',
                    user: { name: '%s', email: '%s' }
                  }, 'http://localhost');
                  window.close();
                </script>
              </body>
            </html>
            """;

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(htmlResponse);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "카카오 로그인 실패: " + e.getMessage());
        }
    }
}
