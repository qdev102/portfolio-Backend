package com.example.demo.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "slides")
@Data // Lombok tự tạo Get/Set cho bạn
public class Slide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
}