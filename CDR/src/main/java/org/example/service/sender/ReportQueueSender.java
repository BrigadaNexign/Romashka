package org.example.service.sender;

import lombok.RequiredArgsConstructor;
import org.example.entity.Fragment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportQueueSender implements ReportSender {
    @Autowired
    private final RabbitTemplate rabbitTemplate;
//    @Value("${spring.rabbitmq.queue.name}")
//    String exchangeName;
//    @Value("${spring.rabbitmq.routingKey}")
//    String routingKey;

    @Override
    public void sendReport(Fragment fragment) {
        rabbitTemplate.convertAndSend("fragment.direct", "fragment", fragment.toString());
        System.out.println("Sent Fragment message: " + fragment.toString());
    }
}
