
package com.fundoo.notes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.event.UserRegistrationEvent;
import com.fundoo.notes.service.EmailService;
import com.fundoo.notes.service.TokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/*
 * Class Name  : UserRegistrationEventConsumer
 * Description : Consumes user registration events from ActiveMQ.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Component
public class UserRegistrationEventConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(UserRegistrationEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final TokenService tokenService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public UserRegistrationEventConsumer(
            ObjectMapper objectMapper,
            EmailService emailService,
            TokenService tokenService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    @JmsListener(destination = "${app.jms.user-registration-queue}")
    public void consume(String message) {

        try {
            UserRegistrationEvent event =
                    objectMapper.readValue(message, UserRegistrationEvent.class);

            logger.info(
                    "Registration event received. User ID: {}, Email: {}",
                    event.userId(),
                    event.email()
            );

            // Generate email verification token (JWT)
            String verificationToken = tokenService.generateToken(event.email());
            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

            // Send real email verification link via Gmail SMTP
            emailService.sendVerificationEmail(event.email(), event.firstName(), verificationLink);

        } catch (Exception exception) {
            logger.error("Failed to process user registration event",
                    exception);
            throw new IllegalStateException(
                    "Unable to process registration event",
                    exception
            );
        }
    }
}
