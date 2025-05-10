package rom.brt.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rom.brt.service.JwtService;

@Configuration
public class FeignConfig {
    private static final Logger logger = LoggerFactory.getLogger(FeignConfig.class);
    private final JwtService jwtService;

    public FeignConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            try {
                String jwtToken = jwtService.generateToken("BRT", "HRS", "calculate"); // изменил на BRT и HRS
                logger.debug("Generated JWT for HRS: {}", jwtToken);
                requestTemplate.header("Authorization", "Bearer " + jwtToken);
            } catch (Exception e) {
                logger.error("Failed to generate JWT: {}", e.getMessage(), e);
                throw new RuntimeException("JWT generation failed", e);
            }
        };
    }
}