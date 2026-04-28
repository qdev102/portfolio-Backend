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

        try {
            // SỬA THÀNH "image" (thay vì "auto" hay "raw")
            // Cloudinary hỗ trợ xử lý PDF trong thư mục image, giúp tạo ảnh bìa và xem trực tiếp!
            Map uploadResult = cloudinary.uploader().upload(tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "use_filename", true
                    ));
            return uploadResult.get("secure_url").toString();
        } finally {
            tempFile.delete();
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