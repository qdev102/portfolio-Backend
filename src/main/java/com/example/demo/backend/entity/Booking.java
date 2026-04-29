package com.example.demo.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phoneNumber;
    private String note;
    private String deadline; // Lưu dạng YYYY-MM-DD
    private String status;   // PENDING, DOING, DONE
//    private String status = "PENDING"; // Mặc định khi khách vừa đặt là PENDING (Chờ xử lý)


    public Booking() {
        this.status = "PENDING";
    }
    private LocalDateTime createdAt = LocalDateTime.now();
}