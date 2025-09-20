package rom.cdr.service.sender;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportQueueSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.cdr-topic:cdr}")
    private String topic;

    private final Logger logger = LoggerFactory.getLogger(ReportQueueSender.class);

    public void sendReport(String message) {
        String key = Integer.toHexString(message != null ? message.hashCode() : 0);

        kafkaTemplate.send(topic, key, message);
        logger.info("Sent CDR to Kafka topic '{}' with key='{}':\n{}\n", topic, key, message);
    }
}
