package com.fundoo.notes.service.impl;

import com.fundoo.notes.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * Class Name  : EmailServiceImpl
 * Description : Sends real-time transactional emails using JavaMailSender and Gmail SMTP.
 * Author      : Ashish Srivastava
 * Date        : 27/11/2026
 */

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:isroasaws@gmail.com}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Fundoo Notes");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            logger.info("Real email successfully sent to: {} with subject: {}", to, subject);
        } catch (Exception e) {
            logger.error("Failed to send real email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Could not send email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        String subject = "Fundoo Notes - Password Reset OTP";
        String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 12px; background-color: #ffffff;">
                <h2 style="color: #5b5bd6; margin-top: 0;">Fundoo Notes</h2>
                <p style="font-size: 15px; color: #333333;">Hello,</p>
                <p style="font-size: 15px; color: #333333;">We received a request to reset your password. Use the verification code below to complete the reset process:</p>
                <div style="margin: 24px 0; text-align: center;">
                    <span style="display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #5b5bd6; background-color: #eef0ff; padding: 12px 24px; border-radius: 8px;">%s</span>
                </div>
                <p style="font-size: 13px; color: #666666;">This OTP is valid for <strong>5 minutes</strong>. If you did not request a password reset, please ignore this email.</p>
                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;" />
                <p style="font-size: 11px; color: #999999; text-align: center;">Fundoo Notes Application &bull; Secure Authentication</p>
            </div>
            """.formatted(otp);

        sendEmail(to, subject, htmlBody);
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to Fundoo Notes!";
        String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 12px; background-color: #ffffff;">
                <h2 style="color: #5b5bd6; margin-top: 0;">Fundoo Notes</h2>
                <p style="font-size: 15px; color: #333333;">Hello <strong>%s</strong>,</p>
                <p style="font-size: 15px; color: #333333;">Welcome to Fundoo Notes! Your account has been registered successfully.</p>
                <p style="font-size: 14px; color: #555555;">Start capturing your notes, organizing with smart labels, and setting reminders right now.</p>
                <hr style="border: none; border-top: 1px solid #eeeeee; margin: 20px 0;" />
                <p style="font-size: 11px; color: #999999; text-align: center;">Fundoo Notes Application &bull; Secure Authentication</p>
            </div>
            """.formatted(name);

        sendEmail(to, subject, htmlBody);
    }
}
