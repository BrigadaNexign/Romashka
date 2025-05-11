package rom.hrs.service.tariff;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.UnsupportedTariffTypeException;

/**
 * Фабрика калькуляторов тарифов.
 * Создает соответствующий калькулятор по типу тарифа.
 */
@Component
@AllArgsConstructor
public class TariffCalculatorFactory {
    private final IntervalTariffCalculator intervalCalculator;
    private final PerMinuteTariffCalculator perMinuteCalculator;
    private final CombinedTariffCalculator combinedCalculator;

    /**
     * Получает калькулятор для указанного тарифа.
     * @param tariff тариф абонента
     * @return калькулятор тарифа
     * @throws UnsupportedTariffTypeException если тип тарифа не поддерживается
     */

    public TariffCalculator getCalculator(Tariff tariff) throws UnsupportedTariffTypeException {
        return switch (tariff.getType()) {
            case 1 -> intervalCalculator;
            case 2 -> perMinuteCalculator;
            case 3 -> combinedCalculator;
            default -> throw new UnsupportedTariffTypeException(tariff.getType());
        };
    }
}
