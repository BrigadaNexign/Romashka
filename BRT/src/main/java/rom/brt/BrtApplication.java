package rom.brt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;

/**
 * Главный класс приложения BRT.
 * Принимает сгенерированные записи из CDR в формате CSV через очередь RabbitMQ.
 * Отправляет запросы в HRS для биллинга, принимает и обрабатывает ответы.
 * Реализует взаимодействие с информацией об обслуживаемых пользователях.
 */
@SpringBootApplication
@EnableFeignClients
@PropertySource("classpath:application.properties")
public class BrtApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrtApplication.class, args);
    }
}