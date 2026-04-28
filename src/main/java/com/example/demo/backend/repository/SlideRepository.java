package com.example.demo.backend.repository;

import com.example.demo.backend.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Long> {
    // Chỉ cần kế thừa JpaRepository, Spring sẽ tự động viết sẵn các lệnh SELECT, INSERT, UPDATE cho bạn
}