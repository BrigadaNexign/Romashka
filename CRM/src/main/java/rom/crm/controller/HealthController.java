package rom.crm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health Check")
public class HealthController {
    private final JdbcTemplate jdbcTemplate;
    private final RestClient restClient;

    @Operation(summary = "Check service health")
    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> healthStatus = new HashMap<>();

        // Check database connectivity
        try {
            jdbcTemplate.execute("SELECT 1");
            healthStatus.put("database", "UP");
        } catch (Exception e) {
            healthStatus.put("database", "DOWN: " + e.getMessage());
        }

        // Check BRT connectivity
        try {
            restClient.get()
                    .uri("http://brt:8081/health")
                    .retrieve()
                    .toBodilessEntity();
            healthStatus.put("brt", "UP");
        } catch (Exception e) {
            healthStatus.put("brt", "DOWN: " + e.getMessage());
        }

        // Check HRS connectivity
        try {
            restClient.get()
                    .uri("http://hrs:8082/health")
                    .retrieve()
                    .toBodilessEntity();
            healthStatus.put("hrs", "UP");
        } catch (Exception e) {
            healthStatus.put("hrs", "DOWN: " + e.getMessage());
        }

        return ResponseEntity.ok(healthStatus);
    }
}