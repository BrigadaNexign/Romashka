package rom.cdr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Основной класс Spring Boot приложения для генерации CDR.
 *
 */
@SpringBootApplication
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {
    /**
     * Точка входа в приложение.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}