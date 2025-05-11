package rom.hrs.service.tariff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;

/**
 * Комбинированный калькулятор тарифов.
 * Объединяет расчеты интервального и поминутного тарифов.
 */
@Component
@RequiredArgsConstructor
public class CombinedTariffCalculator implements TariffCalculator {
    private final IntervalTariffCalculator intervalCalculator;
    private final PerMinuteTariffCalculator perMinuteCalculator;

    @Override
    public CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response) throws BusinessException {
        response = intervalCalculator.calculate(request, tariff, response);
        response = perMinuteCalculator.calculate(request, tariff, response);
        return response;
    }
}
