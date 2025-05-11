package rom.hrs.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.service.CalculationService;

/**
 * Контроллер для обработки запросов расчета стоимости.
 */
@RestController
@RequiredArgsConstructor
public class HrsController {
    private static final Logger logger = LoggerFactory.getLogger(HrsController.class);
    private final CalculationService calculationService;

    @PostMapping("${services.hrs.api.mappings.calculate}")
    public ResponseEntity<CalculationResponse> calculateCost(@RequestBody CalculationRequest request) {
        logger.info("Received request: {}", request);
        ResponseEntity<CalculationResponse> response = calculationService.calculate(request);
        logger.info("Returned response: {}", response);
        return response;
    }
}