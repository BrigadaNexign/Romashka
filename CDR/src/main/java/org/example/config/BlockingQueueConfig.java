package org.example.config;

import org.example.util.FragmentBlockingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlockingQueueConfig {
    private final static int capacity = 1000;
    @Bean
    public FragmentBlockingQueue fragmentBlockingQueue() {
        return new FragmentBlockingQueue(capacity);
    }
}