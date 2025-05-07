package rom.hrs.service.tariff;

import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;

public interface TariffCalculator {
    CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response) throws BusinessException;
}
