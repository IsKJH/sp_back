package sp.spb.auth.naver_authentication.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sp.spb.auth.naver_authentication.service.NaverAuthenticationService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/naver-authentication")
public class NaverAuthenticationController {
    final NaverAuthenticationService naverAuthenticationService;

    @GetMapping("/request-login-url")
    public String getLoginUrl() {
        return naverAuthenticationService.getLoginLink();
    }

    @GetMapping("/login")
    public void processNaverLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {

        try {
            String jwtToken = naverAuthenticationService.processNaverLogin(code);

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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "네이버 로그인 실패: " + e.getMessage());
        }
    }
}
