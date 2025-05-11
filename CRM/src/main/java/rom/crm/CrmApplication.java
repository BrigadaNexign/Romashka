package rom.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Главный класс приложения CRM.
 */
@SpringBootApplication()
@EnableFeignClients(basePackages = {"rom.crm.controller"})
public class CrmApplication {
    /**
     * Точка входа в приложение CRM.
     */
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
}