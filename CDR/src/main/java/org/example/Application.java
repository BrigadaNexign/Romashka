package org.example;

import org.example.service.CDR.GenerationExecutor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Основной класс приложения, который запускает Spring Boot приложение.
 * Использует аннотацию {@link SpringBootApplication} для автоматической настройки и запуска приложения.
 */
@SpringBootApplication
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {
    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки, переданные при запуске приложения
     */
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        GenerationExecutor executor = context.getBean(GenerationExecutor.class);
        executor.generateAllBatches();
    }
}