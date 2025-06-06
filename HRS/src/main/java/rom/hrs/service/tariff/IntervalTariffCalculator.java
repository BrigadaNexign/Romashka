package rom.hrs.service.tariff;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;
import rom.hrs.dto.TariffIntervalDto;
import rom.hrs.entity.Tariff;
import rom.hrs.exception.NoIntervalsFoundException;
import rom.hrs.repository.TariffIntervalRepository;

import java.util.List;

/**
 * Калькулятор интервального тарифа.
 * Рассчитывает стоимость по фиксированным интервальным платежам.
 */
@Component
@RequiredArgsConstructor
public class IntervalTariffCalculator implements TariffCalculator {
    private static final Logger logger = LoggerFactory.getLogger(IntervalTariffCalculator.class);
    private final TariffIntervalRepository tariffIntervalRepository;

    @Override
    public CalculationResponse calculate(CalculationRequest request, Tariff tariff, CalculationResponse response)
            throws NoIntervalsFoundException {
        if (isIntervalPaymentDue(request)) {
            List<TariffIntervalDto> intervals = getIntervalsByTariffId(tariff.getId());

            if (intervals.isEmpty()) throw new NoIntervalsFoundException(tariff.getId());
            if (intervals.size() > 1) logger.warn("Multiple intervals found for tariff {}, using first", tariff.getId());

            applyIntervalFee(request, intervals.get(0), response);
        } else {
            logger.debug(
                    "Payment is not due. Payment day: {} Date of call: {}",
                    request.getCaller().paymentDay(),
                    request.getCurrentDate()
            );
        }
        return response;
    }

    /**
     * Проверяет, наступил ли срок платежа.
     */
    public boolean isIntervalPaymentDue(CalculationRequest request) {
        return request.getCaller().paymentDay().isBefore(request.getCurrentDate());
    }

    public List<TariffIntervalDto> getIntervalsByTariffId(Long tariffId) {
        return tariffIntervalRepository.findAllByTariffId(tariffId).stream()
                .map(TariffIntervalDto::fromEntity)
                .toList();
    }

    /**
     * Применяет интервальный платеж к расчету.
     */
    private void applyIntervalFee(CalculationRequest request, TariffIntervalDto interval, CalculationResponse response) {
        response.setNextPaymentDate(request.getCaller().paymentDay().plusDays(interval.getInterval()));
        double currentCost = response.getCost() != null ? response.getCost() : 0.0;
        response.setCost(currentCost + interval.getPrice().doubleValue());
    }
}