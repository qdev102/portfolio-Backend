package com.example.demo.backend.repository;

import com.example.demo.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Hàm này giúp Spring tự động tìm user theo tên đăng nhập
    Optional<User> findByUsername(String username);
}