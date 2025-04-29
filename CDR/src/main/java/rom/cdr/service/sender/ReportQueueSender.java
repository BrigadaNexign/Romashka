package rom.cdr.service.sender;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportQueueSender {
    @Autowired
    private final RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.exchange.name}")
    String exchangeName;
    @Value("${spring.rabbitmq.routing.key}")
    String routingKey;
    private final Logger logger = LoggerFactory.getLogger(ReportQueueSender.class);

    public void sendReport(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        logger.info("Sent cdr:\n{}\n", message);
    }
}
