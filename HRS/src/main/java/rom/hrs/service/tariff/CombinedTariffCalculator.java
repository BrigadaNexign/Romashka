package rom.hrs.service.tariff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;

@Component
public class CombinedTariffCalculator implements TariffCalculator {
    private final IntervalTariffCalculator intervalCalculator;
    private final PerMinuteTariffCalculator perMinuteCalculator;

    @Autowired
    public CombinedTariffCalculator(
            IntervalTariffCalculator intervalCalculator,
            PerMinuteTariffCalculator perMinuteCalculator
    ) {
        this.intervalCalculator = intervalCalculator;
        this.perMinuteCalculator = perMinuteCalculator;
    }

    @Override
    public CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response) throws BusinessException {
        response = intervalCalculator.calculate(request, tariff, response);
        response = perMinuteCalculator.calculate(request, tariff, response);
        return response;
    }
}
