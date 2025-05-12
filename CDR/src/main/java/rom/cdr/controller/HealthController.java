package rom.cdr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> healthStatus = new HashMap<>();
        try {
            jdbcTemplate.execute("SELECT 1");
            healthStatus.put("database", "UP");
        } catch (Exception e) {
            healthStatus.put("database", "DOWN: " + e.getMessage());
        }

        return ResponseEntity.ok(healthStatus);
    }
}