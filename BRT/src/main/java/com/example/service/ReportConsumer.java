package com.example.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
public class ReportConsumer {
    @RabbitListener(queues = "cdr.queue")
    public void handleMessage(String message) {
        System.out.println("Received message: \n" + message);
    }
}
