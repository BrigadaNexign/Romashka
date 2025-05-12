package rom.hrs.service.tariff;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;
import rom.hrs.service.CallPricingService;

/**
 * Калькулятор поминутного тарифа.
 * Рассчитывает стоимость по количеству минут разговора.
 */
@Component
@RequiredArgsConstructor
public class PerMinuteTariffCalculator implements TariffCalculator {
    private final static Logger logger = LoggerFactory.getLogger(PerMinuteTariffCalculator.class);
    private final CallPricingService pricingService;

    @Override
    public CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response)
            throws BusinessException {
        if (hasFreeMinutes(request)) {
            applyFreeMinutes(request, tariff, response);
        } else {
            logger.debug("No available minutes. Charging fees");
            pricingService.applyCallPricing(request, tariff, response);
        }
        return response;
    }

    public boolean hasFreeMinutes(CalculationRequest request) {
        return request.getCaller().minutes() > 0;
    }

    /**
     * Применяет бесплатные минуты к расчету.
     */
    void applyFreeMinutes(CalculationRequest request, Tariff tariff, CalculationResponse response)
            throws BusinessException {
        logger.debug("Caller {} from request {} has available minutes", request.getCaller(), request);
        int remainingMinutes = request.getCaller().minutes() - request.getDurationMinutes();
        if (remainingMinutes >= 0) {
            response.setRemainingMinutes(remainingMinutes);
        } else {
            logger.debug("Amount of available minutes was less than call duration. Charging fees");
            response.setRemainingMinutes(0);
            request.setDurationMinutes(Math.abs(remainingMinutes));
            pricingService.applyCallPricing(request, tariff, response);
        }
    }
}