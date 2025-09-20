package rom.brt.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

/**
 * Потребитель сообщений из Kafka.
 * Получает CDR-отчеты и передает их на обработку.
 */
@Service
@RequiredArgsConstructor
public class ReportConsumer {

    private final MessageHandler messageHandler;
    private final Logger logger = LoggerFactory.getLogger(ReportConsumer.class);

    /**
     * Обрабатывает сообщение из топика CDR.
     *
     * @param message сообщение в формате CSV
     */
    @KafkaListener(
            topics = "${app.kafka.cdr-topic:cdr}",
            groupId = "${spring.kafka.consumer.group-id:brt}"
    )
    public void handleMessage(
            String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment ack,
            ConsumerRecord<String, String> record
    ) {
        try {
            logger.info("Received message (partition={}, offset={}, key={}):\n{}\n",
                    record.partition(), record.offset(), key, message);

            messageHandler.handleMessage(message);

            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Error while processing message at offset {}: {}", record.offset(), e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
