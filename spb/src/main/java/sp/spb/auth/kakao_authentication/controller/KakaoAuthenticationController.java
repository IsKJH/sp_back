package sp.spb.auth.kakao_authentication.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sp.spb.auth.kakao_authentication.service.KakaoAuthenticationService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao-authentication")
public class KakaoAuthenticationController {
    final private KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/request-login-url")
    public String getLoginUrl() {
        return kakaoAuthenticationService.getLoginLink();
    }

    @GetMapping("/login")
    public void processKakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

        try {
            String jwtToken = kakaoAuthenticationService.processKakaoLogin(code);

            String htmlResponse = """
                    <html>
                      <body>
                        <script>
                          window.opener.postMessage({
                            accessToken: '%s',
                            success: true
                          }, 'http://localhost');
                          window.close();
                        </script>
                      </body>
                    </html>
                    """.formatted(jwtToken);

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(htmlResponse);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "카카오 로그인 실패: " + e.getMessage());
        }
    }
}
