package com.fundoo.notes.service;

/*
 * Interface Name : EmailService
 * Description    : Defines operations for sending real-time emails via SMTP.
 * Author         : Ashish Srivastava
 * Date           : 27/11/2026
 */

public interface EmailService {

    // Sends a plain text or HTML email to the specified recipient.
    void sendEmail(String to, String subject, String body);

    // Sends an OTP email for password reset.
    void sendOtpEmail(String to, String otp);

    // Sends a registration welcome / verification email.
    void sendWelcomeEmail(String to, String name);

    // Sends an email verification link.
    void sendVerificationEmail(String to, String name, String verificationLink);
}
