package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

//    public String uploadImage(MultipartFile file) throws IOException {
//        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
//        file.transferTo(tempFile); // Dùng lệnh này an toàn hơn, chống hỏng file
//
//        // Phân loại: Ảnh thì "auto", Tài liệu (PDF, PPT, DOC) thì BẮT BUỘC "raw" (nguyên bản)
//        String originalFilename = file.getOriginalFilename();
//        String resourceType = "auto";
//        if (originalFilename != null && originalFilename.toLowerCase().matches(".*\\.(pdf|ppt|pptx|doc|docx)$")) {
//            resourceType = "raw";
//        }
//
//        try {
//            Map uploadResult = cloudinary.uploader().upload(tempFile,
//                    ObjectUtils.asMap(
//                            "resource_type", resourceType,
//                            "use_filename", true
//                    ));
//            return uploadResult.get("secure_url").toString();
//        } finally {
//            tempFile.delete();
//        }
//    }


    public String uploadImage(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
        file.transferTo(tempFile);

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";

        // 🌟 BỘ PHÂN LUỒNG THÔNG MINH
        String resourceType = "auto";
        if (fileName.matches(".*\\.(jpg|jpeg|png|gif|webp|pdf)$")) {
            // Nhóm 1: Ảnh và PDF -> Bắt buộc vào thư mục 'image' để tạo ảnh bìa xem trước
            resourceType = "image";
        } else if (fileName.matches(".*\\.(ppt|pptx|doc|docx|xls|xlsx|zip)$")) {
            // Nhóm 2: File Office và các file khác -> Bắt buộc vào thư mục 'raw' (tệp thô)
            resourceType = "raw";
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(tempFile,
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "use_filename", true
                    ));
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            // In lỗi ra log của Railway để dễ bắt bệnh nếu có lỗi tiếp
            System.out.println("❌ LỖI CLOUDINARY: " + e.getMessage());
            throw new RuntimeException("Upload thất bại: " + e.getMessage());
        } finally {
            tempFile.delete(); // Dọn dẹp file tạm
        }
    }


    public void deleteImage(String publicId) throws IOException {
        // CHIÊU THỨC XÓA TRIỆT ĐỂ:
        // File PPT được Cloudinary lưu dạng "raw", còn Ảnh/PDF lưu dạng "image".
        // Ta vung kiếm xóa cả 2 nơi để đảm bảo 100% không trượt tệp nào!
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
    }
}