package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Потребитель сообщений из очереди RabbitMQ.
 * Получает CDR-отчеты и передает их на обработку.
 */
@Service
@RequiredArgsConstructor
public class ReportConsumer {
    private final MessageHandler messageHandler;
    private final Logger logger = LoggerFactory.getLogger(ReportConsumer.class);

    /**
     * Обрабатывает сообщение из очереди CDR.
     *
     * @param message сообщение в формате CSV
     */
    @RabbitListener(queues = "cdr.queue")
    public void handleMessage(String message) {
        logger.info("Received message: \n{}", message);
        messageHandler.handleMessage(message);
    }
}
