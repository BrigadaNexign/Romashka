package org.example.service.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportQueueSender {
    @Autowired
    private final RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.queue.name}")
    String exchangeName;
    @Value("${spring.rabbitmq.routing.key}")
    String routingKey;

    public void sendReport(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        System.out.println("Sent cdr: " + message);
    }
}
