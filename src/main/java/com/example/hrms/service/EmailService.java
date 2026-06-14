package com.example.hrms.service;

import com.example.hrms.entity.EmployeeProfile;
import com.example.hrms.entity.LeaveRequest;
import com.example.hrms.entity.LeaveStatus;
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
    public void sendLeaveStatusEmail(EmployeeProfile employee, LeaveRequest leave, LeaveStatus newStatus, String comment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(employee.getUser().getEmail());
            
            String statusText = newStatus == LeaveStatus.HR_APPROVED ? "Approved" : "Rejected";
            helper.setSubject("Leave Request " + statusText + " - HRMS");

            String commentSection = (comment != null && !comment.trim().isEmpty()) 
                ? "<li><b>Manager/HR Comment:</b> " + comment + "</li>" 
                : "";

            String htmlContent = String.format(
                    "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                    "<h2 style='color: #0f172a;'>Hello %s,</h2>" +
                    "<p>Your leave request has been <b style='color: %s;'>%s</b>.</p>" +
                    "<h3 style='border-bottom: 1px solid #ddd; padding-bottom: 8px;'>Leave Details:</h3>" +
                    "<ul>" +
                    "<li><b>Leave Type:</b> %s</li>" +
                    "<li><b>Start Date:</b> %s</li>" +
                    "<li><b>End Date:</b> %s</li>" +
                    "<li><b>Reason:</b> %s</li>" +
                    "%s" +
                    "</ul>" +
                    "<p style='margin-top: 20px;'>If you have any questions, please contact your HR or Manager.</p>" +
                    "<p style='color: #64748b; font-size: 0.9em;'>Best Regards,<br/>HRMS Team</p>" +
                    "</div>" +
                    "</body></html>",
                    employee.getFullName(),
                    newStatus == LeaveStatus.HR_APPROVED ? "#10b981" : "#ef4444",
                    statusText,
                    leave.getLeaveType(),
                    leave.getStartDate().toString(),
                    leave.getEndDate().toString(),
                    leave.getReason(),
                    commentSection
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send leave status email to: " + employee.getUser().getEmail());
            e.printStackTrace();
        }
    }
}
