
package com.fundoo.notes.service.impl;

import com.fundoo.notes.event.ReminderEvent;
import com.fundoo.notes.service.ReminderEventPublisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 * Class Name  : RabbitMQReminderProducer
 * Description : Publishes reminder events to RabbitMQ.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Service
public class RabbitMQReminderProducer implements ReminderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitMQReminderProducer(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.reminder-exchange}") String exchange,
            @Value("${app.rabbitmq.reminder-routing-key}") String routingKey) {

        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publish(ReminderEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
