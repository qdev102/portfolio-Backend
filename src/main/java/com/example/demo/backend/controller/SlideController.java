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
import java.util.Map;

@RestController
@RequestMapping("/api/slides")
@CrossOrigin(origins = "*")
public class SlideController {

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // API lấy danh sách Slide
    @GetMapping
    public List<Slide> getAllSlides() {
        return slideRepository.findAll();
    }

    // API Thêm Slide
    @PostMapping
    public Slide createSlide(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category) throws IOException {

        // 1. Upload file lên Cloudinary và lấy link URL
        String imageUrl = cloudinaryService.uploadImage(file);

        // 2. Tạo đối tượng Slide và gắn thông tin
        Slide newSlide = new Slide();
        newSlide.setTitle(title);
        newSlide.setDescription(description);
        newSlide.setCategory(category);
        newSlide.setImageUrl(imageUrl);

        // 3. Lưu vào MySQL
        return slideRepository.save(newSlide);
    }

    // API Xóa Slide
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlide(@PathVariable Long id) {
        return slideRepository.findById(id)
                .map(slide -> {
                    // ==========================================
                    // BƯỚC 1: TIÊU DIỆT FILE TRÊN CLOUDINARY
                    // ==========================================
                    if (slide.getImageUrl() != null && slide.getImageUrl().contains("cloudinary")) {
                        try {
                            String imageUrl = slide.getImageUrl();

                            // Lấy tên file ở cuối đường link (VD: upload_123_slide.pdf)
                            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                            // Lấy tên file bỏ đuôi (VD: upload_123_slide)
                            String publicIdNoExt = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;

                            // Bắn lệnh xóa 2 lần để đảm bảo không lọt lưới tệp nào!
                            // - Xóa tên không đuôi: Để tiêu diệt Ảnh (Image)
                            // - Xóa tên có đuôi: Để tiêu diệt tài liệu PDF/PPT (Raw)
                            cloudinaryService.deleteImage(publicIdNoExt);
                            cloudinaryService.deleteImage(filename);

                        } catch (Exception e) {
                            System.out.println("Lỗi cảnh báo khi xóa file Cloudinary: " + e.getMessage());
                        }
                    }

                    // ==========================================
                    // BƯỚC 2: XÓA DỮ LIỆU TRONG MYSQL
                    // ==========================================
                    slideRepository.delete(slide);
                    return ResponseEntity.ok(Map.of("message", "Xóa slide và dọn rác thành công!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}