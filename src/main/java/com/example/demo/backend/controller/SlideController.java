package com.example.demo.backend.controller;


import com.example.demo.backend.entity.Slide;
import com.example.demo.backend.repository.SlideRepository;
import com.example.demo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slides")
@CrossOrigin(origins = "*")
public class SlideController {

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // API cũ: Lấy danh sách Slide (dùng cho Landing Page)
    @GetMapping
    public List<Slide> getAllSlides() {
        return slideRepository.findAll();
    }

    // API mới: Thêm Slide (dùng cho Admin)
    @PostMapping
    public Slide createSlide(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category) throws IOException {

        // 1. Upload ảnh lên Cloudinary và lấy link URL
        String imageUrl = cloudinaryService.uploadImage(file);

        // 2. Tạo đối tượng Slide và gắn thông tin
        Slide newSlide = new Slide();
        newSlide.setTitle(title);
        newSlide.setDescription(description);
        newSlide.setCategory(category);
        newSlide.setImageUrl(imageUrl); // Lưu cái link ảnh vừa lấy được

        // 3. Lưu vào MySQL
        return slideRepository.save(newSlide);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlide(@PathVariable Long id) {
        return slideRepository.findById(id)
                .map(slide -> {
                    slideRepository.delete(slide);
                    return ResponseEntity.ok(Map.of("message", "Xóa slide thành công!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}