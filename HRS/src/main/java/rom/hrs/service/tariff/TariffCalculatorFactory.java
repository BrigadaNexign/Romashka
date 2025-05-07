package rom.hrs.service.tariff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.UnsupportedTariffTypeException;

@Component
public class TariffCalculatorFactory {
    private final IntervalTariffCalculator intervalCalculator;
    private final PerMinuteTariffCalculator perMinuteCalculator;
    private final CombinedTariffCalculator combinedCalculator;

    @Autowired
    public TariffCalculatorFactory(
            IntervalTariffCalculator intervalCalculator,
            PerMinuteTariffCalculator perMinuteCalculator,
            CombinedTariffCalculator combinedCalculator
    ) {
        this.intervalCalculator = intervalCalculator;
        this.perMinuteCalculator = perMinuteCalculator;
        this.combinedCalculator = combinedCalculator;
    }

    public TariffCalculator getCalculator(Tariff tariff) throws UnsupportedTariffTypeException {
        return switch (tariff.getType()) {
            case 1 -> intervalCalculator;
            case 2 -> perMinuteCalculator;
            case 3 -> combinedCalculator;
            default -> throw new UnsupportedTariffTypeException(tariff.getType());
        };
    }
}
