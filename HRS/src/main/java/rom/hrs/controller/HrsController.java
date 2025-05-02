package rom.hrs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.service.CalculationService;

@RestController
public class HrsController {
    private static final Logger logger = LoggerFactory.getLogger(HrsController.class);
    @Autowired
    private CalculationService calculationService;

    @PostMapping("/hrs/calculate")
    public CalculationResponse calculateCost(@RequestBody CalculationRequest request) {
        logger.info("Received request: {}", request);
        return calculationService.calculate(request);
    }

}