
package com.fundoo.notes.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/*
 * Class Name  : RabbitMQConfig
 * Description : Configures RabbitMQ queue, exchange and message conversion.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${app.rabbitmq.reminder-queue}")
    private String reminderQueue;

    @Value("${app.rabbitmq.reminder-exchange}")
    private String reminderExchange;

    @Value("${app.rabbitmq.reminder-routing-key}")
    private String reminderRoutingKey;

    @Bean
    public Queue reminderQueue() {
        return QueueBuilder.durable(reminderQueue).build();
    }

    @Bean
    public DirectExchange reminderExchange() {
        return new DirectExchange(reminderExchange);
    }

    @Bean
    public Binding reminderBinding() {
        return BindingBuilder.bind(reminderQueue())
                .to(reminderExchange())
                .with(reminderRoutingKey);
    }

    @Bean
    public MessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
