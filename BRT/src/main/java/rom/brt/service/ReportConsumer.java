package rom.brt.service;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ReportConsumer {
    @Autowired
    private MessageHandler messageHandler;
    @RabbitListener(queues = "cdr.queue")
    public void handleMessage(String message) {
        messageHandler.handleMessage(message);
        System.out.println("Received message: \n" + message);
    }
}
