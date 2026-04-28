package com.example.demo.service;


import com.example.demo.backend.entity.Booking;
import com.example.demo.backend.entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${admin.email}")
    private String adminEmail;

    public void sendBookingNotification(Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Hệ thống Portfolio <no-reply@portfolio.com>");
        message.setTo(adminEmail);
        message.setSubject("🔔 CÓ KHÁCH HÀNG ĐẶT LỊCH MỚI!");

        String content = "Bạn vừa nhận được một yêu cầu thiết kế mới:\n\n" +
                "👤 Khách hàng: " + booking.getCustomerName() + "\n" +
                "📞 Số điện thoại: " + booking.getPhoneNumber() + "\n" +
                "📝 Ghi chú/Yêu cầu: " + (booking.getNote() != null ? booking.getNote() : "Không có") + "\n\n" +
                "--- Vui lòng kiểm tra Admin Dashboard để xử lý ---";

        message.setText(content);
        mailSender.send(message);
    }

}