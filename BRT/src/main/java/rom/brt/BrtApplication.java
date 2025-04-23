package rom.brt;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableRabbit
@ComponentScan
public class BrtApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(BrtApplication.class, args);
            System.out.println("BrtApplication started successfully");
        } catch (Exception e) {
            System.err.println("BrtApplication failed to start: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}