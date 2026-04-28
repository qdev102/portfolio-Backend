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

    public String uploadImage(MultipartFile file) throws IOException {
        // 1. CHIÊU THỨC QUAN TRỌNG: Tạo file vật lý tạm thời để giữ nguyên ĐUÔI FILE (.pdf, .pptx)
        File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }

        try {
            // 2. Upload file lên. Cloudinary sẽ tự soi đuôi file để biết đây là Ảnh hay PDF/PPT
            Map uploadResult = cloudinary.uploader().upload(tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "use_filename", true
                    ));

            // 3. BẮT BUỘC dùng "secure_url" (HTTPS). Dùng "url" (HTTP) Vercel sẽ chặn tải file!
            return uploadResult.get("secure_url").toString();
        } finally {
            // 4. Xóa file tạm trên server sau khi up xong
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