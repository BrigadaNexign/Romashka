package org.example.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

//    @Value("${rabbitmq.host}")
//    private String host;
//
//    @Value("${rabbitmq.port}")
//    private int port;
//
//    @Value("${rabbitmq.username}")
//    private String username;
//
//    @Value("${rabbitmq.password}")
//    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(host);
//        connectionFactory.setPort(port);
//        connectionFactory.setUsername(username);
//        connectionFactory.setPassword(password);
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

    @Bean
    public Queue cdrQueue() {
        return new Queue("cdr.queue", true); // durable queue
    }

    @Bean
    public DirectExchange cdrDirectExchange() {
        return new DirectExchange("cdr.direct", true, false);
    }

    @Bean
    public Binding cdrBinding() {
        return BindingBuilder.bind(cdrQueue())
                .to(cdrDirectExchange())
                .with("cdr");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
