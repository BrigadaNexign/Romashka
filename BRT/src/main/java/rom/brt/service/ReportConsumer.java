package rom.brt.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ReportConsumer {
    @Autowired
    private MessageHandler messageHandler;
    private final Logger logger = LoggerFactory.getLogger(ReportConsumer.class);

    @RabbitListener(queues = "cdr.queue")
    public void handleMessage(String message) {
        logger.info("Received message: \n{}", message);
        messageHandler.handleMessage(message);
    }
}
