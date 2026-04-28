package com.example.demo.backend.controller;



 import com.example.demo.dto.LoginRequest;
 import com.example.demo.backend.repository.UserRepository;
 import com.example.demo.backend.entity.User;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. Tìm user trong database
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 2. Kiểm tra mật khẩu (So sánh chữ "admin123" với chuỗi mã hóa trong DB)
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // 3. Nếu đúng, tạo thẻ JWT
                String token = jwtUtil.generateToken(user.getUsername());
                // Trả thẻ về cho Frontend
                return ResponseEntity.ok(Map.of("token", token, "message", "Đăng nhập thành công"));
            }
        }

        // Nếu sai tên hoặc sai mật khẩu
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Sai tài khoản hoặc mật khẩu"));
    }
}