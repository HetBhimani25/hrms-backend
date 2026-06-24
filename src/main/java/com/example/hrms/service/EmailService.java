package com.example.hrms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${hrms.mail.from:noreply@hrms.com}")
    private String fromEmail;

    @Value("${SENDGRID_API_KEY:}")
    private String sendGridApiKey;

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            if (sendGridApiKey == null || sendGridApiKey.trim().isEmpty()) {
                System.err.println("SENDGRID_API_KEY is not set. Cannot send email to " + toEmail);
                return;
            }

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

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.sendgrid.com/v3/mail/send";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(sendGridApiKey);

            Map<String, Object> body = new HashMap<>();
            
            Map<String, Object> personalization = new HashMap<>();
            personalization.put("to", List.of(Map.of("email", toEmail)));
            personalization.put("subject", "Reset Your Password - HRMS");
            body.put("personalizations", List.of(personalization));

            body.put("from", Map.of("email", fromEmail));
            
            Map<String, String> content = new HashMap<>();
            content.put("type", "text/html");
            content.put("value", htmlContent);
            body.put("content", List.of(content));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Successfully sent password reset email to: " + toEmail);
            } else {
                System.err.println("Failed to send email. SendGrid responded with: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Failed to send password reset email to: " + toEmail);
            e.printStackTrace();
        }
    }
}
