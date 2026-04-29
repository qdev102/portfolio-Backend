package com.example.demo.backend.controller;


import com.example.demo.backend.entity.Booking;
import com.example.demo.backend.repository.BookingRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    // --- HÀM 1: LẤY DANH SÁCH (Để hiển thị lên Dashboard) ---
    @GetMapping
    public List<Booking> getAllBookings() {
        // Trả về toàn bộ danh sách khách hàng từ Database
        return bookingRepository.findAll();
    }

    // --- HÀM 2: TẠO MỚI (Khi khách bấm đặt lịch + Gửi Mail) ---
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        // 1. Lưu vào MySQL
        Booking savedBooking = bookingRepository.save(booking);

        // 2. Gửi Mail thông báo (Chạy ngầm để khách không phải đợi)
        new Thread(() -> {
            try {
                emailService.sendBookingNotification(savedBooking);
            } catch (Exception e) {
                System.err.println("Lỗi gửi mail: " + e.getMessage());
            }
        }).start();

        return ResponseEntity.ok(savedBooking);
    }

    @PatchMapping("/{id}/status")
    public Booking updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setStatus(statusMap.get("status"));
        return bookingRepository.save(booking);
    }
}