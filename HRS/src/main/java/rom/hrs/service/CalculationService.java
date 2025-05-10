package rom.hrs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        // Validate input
        if (request == null || request.getCaller() == null || request.getCaller().tariffId() == null) {
            logger.warn("Invalid request: null or missing caller/tariffId");
            return ResponseEntity.badRequest().body(responseBuilder.createErrorResponse(
                    new IllegalArgumentException("Invalid calculation request")));
        }

        try {
            logger.debug("Fetching tariff for ID: {}", request.getCaller().tariffId());
            Tariff tariff = tariffService.findTariffById(request.getCaller().tariffId());
            if (tariff == null) {
                logger.error("No tariff found for ID: {}", request.getCaller().tariffId());
                throw new NoTariffFoundException(request.getCaller().tariffId());
            }

            logger.debug("Selecting calculator for tariff: {}", tariff);
            TariffCalculator calculator = calculatorFactory.getCalculator(tariff);
            logger.debug("Initializing response for request");
            CalculationResponse response = responseBuilder.initResponse(request, tariff);
            logger.debug("Calculating response");
            response = calculator.calculate(request, tariff, response);
            logger.debug("Filling default fields in response");
            CalculationResponse fullResponse = responseBuilder.fillDefaultFields(request, tariff, response);

            logger.debug("Validating response");
            responseBuilder.validateSuccessfulResponse(fullResponse);

            logger.info("Calculation completed successfully for tariff ID: {}", request.getCaller().tariffId());
            return ResponseEntity.ok(fullResponse);

        } catch (NoTariffFoundException e) {
            logger.error("Tariff not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.createErrorResponse(e));

        } catch (IncompleteResponseException e) {
            logger.error("Incomplete response: {}", e.getMessage());
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(responseBuilder.createErrorResponse(e));

        } catch (Exception e) {
            logger.error("Unexpected error during calculation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseBuilder.createErrorResponse(e));
        }
    }
}