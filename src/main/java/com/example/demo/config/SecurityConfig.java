package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. CHUẨN HÓA NGUỒN TRUY CẬP (Thêm dấu * để dự phòng nếu link Vercel đổi)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://portfolio-frontend-smoky-sigma.vercel.app",
                "http://localhost:3000",
                "https://*.vercel.app"
        ));

        // 2. MỞ KHÓA TẤT CẢ PHƯƠNG THỨC (Bao gồm cả DELETE)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. MỞ KHÓA TẤT CẢ HEADERS (Cực kỳ quan trọng để Frontend gửi Token)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // 4. MỞ KHÓA BẢO MẬT HIỂN THỊ (Cho phép nhúng file PDF lên web)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Mở đường cho Preflight Request
                        .requestMatchers(HttpMethod.GET, "/api/slides").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated() // Lệnh DELETE sẽ chạy vào đây, yêu cầu Token
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}