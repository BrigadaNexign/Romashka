package rom.cdr.service.sender;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис для отправки записей CDR в RabbitMQ очередь.
 * Обеспечивает асинхронную передачу данных о звонках в формате CSV.
 */
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

    /**
     * Отправляет сообщение с CDR данными в RabbitMQ очередь.
     *
     * @param message строка с данными о звонках в CSV формате
     * @throws org.springframework.amqp.AmqpException при ошибках отправки
     */
    public void sendReport(String message) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        logger.info("Sent cdr:\n{}\n", message);
    }
}
