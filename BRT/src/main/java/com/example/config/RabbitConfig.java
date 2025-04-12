package com.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${spring.rabbitmq.queue.name}") private String queueName;
    @Value("${spring.rabbitmq.exchange.name}") private String exchangeName;
    @Value("${spring.rabbitmq.routing.key}") private String routingKey;
    @Value("${spring.rabbitmq.host}") private String host;
    @Value("${spring.rabbitmq.port}") private int port;
    @Value("${spring.rabbitmq.username}") private String username;
    @Value("${spring.rabbitmq.password}") private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue())
                .to(exchange())
                .with(routingKey);
    }

}