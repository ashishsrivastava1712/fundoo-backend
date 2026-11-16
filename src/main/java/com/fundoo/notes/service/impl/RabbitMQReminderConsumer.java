
package com.fundoo.notes.service.impl;

import com.fundoo.notes.event.ReminderEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/*
 * Class Name  : RabbitMQReminderConsumer
 * Description : Consumes reminder events from RabbitMQ.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Component
public class RabbitMQReminderConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(RabbitMQReminderConsumer.class);

    @RabbitListener(queues = "${app.rabbitmq.reminder-queue}")
    public void consume(ReminderEvent event) {

        logger.info(
                "Reminder event received. User ID: {}, Note ID: {}, Email: {}, Reminder Time: {}",
                event.userId(),
                event.noteId(),
                event.email(),
                event.reminderTime()
        );
    }
}
