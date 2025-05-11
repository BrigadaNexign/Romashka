package rom.hrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Главный класс приложения HRS.
 */
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class HrsApplication {
    /**
     * Точка входа в приложение HRS.
     */
    public static void main(String[] args) {
        SpringApplication.run(HrsApplication.class, args);
    }
}