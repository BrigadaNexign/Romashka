package rom.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.IncompleteResponseException;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.service.tariff.TariffCalculator;
import rom.hrs.service.tariff.TariffCalculatorFactory;

@Service
public class CalculationService {
    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class);

    private final TariffService tariffService;
    private final TariffCalculatorFactory calculatorFactory;
    private final ResponseBuilder responseBuilder;

    @Autowired
    public CalculationService(
            TariffService tariffService,
            TariffCalculatorFactory calculatorFactory,
            ResponseBuilder responseBuilder
    ) {
        this.tariffService = tariffService;
        this.calculatorFactory = calculatorFactory;
        this.responseBuilder = responseBuilder;
    }

    public ResponseEntity<CalculationResponse> calculate(CalculationRequest request) {
        try {
            Tariff tariff = tariffService.findTariffById(request.getCaller().tariffId());
            if (tariff == null) {
                throw new NoTariffFoundException(request.getCaller().tariffId());
            }

            TariffCalculator calculator = calculatorFactory.getCalculator(tariff);
            CalculationResponse response = responseBuilder.initResponse(request, tariff);
            response = calculator.calculate(request, tariff, response);
            CalculationResponse fullResponse = responseBuilder.fillDefaultFields(request, tariff, response);

            responseBuilder.validateSuccessfulResponse(fullResponse);

            return ResponseEntity.ok(fullResponse);

        } catch (IncompleteResponseException e) {
            logger.error("Incomplete response: {}", e.getMessage());
            return ResponseEntity
                    .status(e.getErrorCode().getHttpStatus())
                    .body(responseBuilder.createErrorResponse(e));

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseBuilder.createErrorResponse(e));
        }
    }
}