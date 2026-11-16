
package com.fundoo.notes.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoo.notes.event.UserRegistrationEvent;
import com.fundoo.notes.service.UserRegistrationEventPublisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/*
 * Class Name  : JmsUserRegistrationEventPublisher
 * Description : Publishes user registration events to ActiveMQ.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Service
public class JmsUserRegistrationEventPublisher
        implements UserRegistrationEventPublisher {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final String registrationQueue;

    public JmsUserRegistrationEventPublisher(
            JmsTemplate jmsTemplate,
            ObjectMapper objectMapper,
            @Value("${app.jms.user-registration-queue}") String registrationQueue) {

        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.registrationQueue = registrationQueue;
    }

    @Override
    public void publish(UserRegistrationEvent event) {

        try {
            String message = objectMapper.writeValueAsString(event);

            jmsTemplate.convertAndSend(registrationQueue, message);

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to serialize user registration event",
                    exception
            );
        }
    }
}
