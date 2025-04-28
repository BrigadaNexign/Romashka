package rom.brt;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@EnableFeignClients
@EnableRabbit
@PropertySource("classpath:application.properties")
public class BrtApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrtApplication.class, args);
    }
}