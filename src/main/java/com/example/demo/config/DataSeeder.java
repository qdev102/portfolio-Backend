package com.example.demo.config;

import com.example.demo.backend.repository.UserRepository;
import com.example.demo.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername("admin");

        if (userOpt.isEmpty()) {
            // Nếu chưa có tài khoản admin thì tạo mới
            User admin = new User();
            admin.setUsername("admin");
            // Spring Security sẽ tự động mã hóa chữ "admin123" thành chuẩn BCrypt thật
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("========== ĐÃ TẠO TÀI KHOẢN ADMIN THÀNH CÔNG ==========");
        } else {
            // Nếu đã có nhưng sai mật khẩu (như trường hợp của bạn), ta cập nhật lại luôn
            User admin = userOpt.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            System.out.println("========== ĐÃ CẬP NHẬT LẠI MẬT KHẨU ADMIN CHUẨN ==========");
        }
    }
}