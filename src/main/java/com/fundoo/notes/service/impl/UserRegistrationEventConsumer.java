
package com.fundoo.notes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.event.UserRegistrationEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public UserRegistrationEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
