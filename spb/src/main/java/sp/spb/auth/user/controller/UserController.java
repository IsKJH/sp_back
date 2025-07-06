package sp.spb.auth.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sp.spb.auth.user.entity.User;
import sp.spb.auth.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        String email = authentication.getName(); // JWT에서 추출된 이메일
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("createdAt", user.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        String email = authentication.getName();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "안녕하세요! " + email + "님");
        response.put("data", "이것은 보호된 API입니다.");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test(Authentication authentication) {
        return ResponseEntity.ok("JWT 인증 성공! 사용자: " + authentication.getName());
    }
}