package org.example;

import org.example.service.CDR.CDRGeneratorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Основной класс приложения, который запускает Spring Boot приложение.
 * Использует аннотацию {@link SpringBootApplication} для автоматической настройки и запуска приложения.
 */
@SpringBootApplication
public class Application {
    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки, переданные при запуске приложения
     */
    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        CDRGeneratorService service = context.getBean(CDRGeneratorService.class);
        service.generateAllBatches();
    }
}