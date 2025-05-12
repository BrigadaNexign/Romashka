package rom.hrs.service.tariff;

import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.*;

/**
 * Интерфейс калькулятора стоимости звонка для тарифов.
 * Определяет основной метод для расчета стоимости звонка.
 */
public interface TariffCalculator {
    /**
     * Выполняет расчет стоимости звонка.
     * @param request данные запроса
     * @param tariff тариф абонента
     * @param response объект ответа для заполнения
     * @return расчетный ответ
     * @throws BusinessException при ошибках бизнес-логики
     */
    CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response) throws BusinessException;
}
