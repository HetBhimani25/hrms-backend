package com.example.hrms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${hrms.mail.from:noreply@hrms.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password - HRMS");

            String htmlContent = String.format(
                    "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                    "<h2 style='color: #0f172a;'>Password Reset Request</h2>" +
                    "<p>We received a request to reset your password for your HRMS account.</p>" +
                    "<p>Click the button below to reset it. This link is valid for 15 minutes.</p>" +
                    "<a href=\"%s\" style=\"display: inline-block; padding: 12px 24px; margin: 20px 0; font-size: 16px; color: #fff; background-color: #3b82f6; text-decoration: none; border-radius: 6px;\">Reset Password</a>" +
                    "<p>If you did not request a password reset, please ignore this email.</p>" +
                    "<p style='margin-top: 30px; color: #64748b; font-size: 0.9em;'>Best Regards,<br/>HRMS Team</p>" +
                    "</div>" +
                    "</body></html>",
                    resetLink
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email to: " + toEmail);
            e.printStackTrace();
        }
    }
}
