package com.example.demo.backend.repository;


import com.example.demo.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Spring Boot sẽ lo phần SQL lưu dữ liệu
    List<Booking> findAllByOrderByDeadlineAsc();
}