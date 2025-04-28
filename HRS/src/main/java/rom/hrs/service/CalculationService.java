package rom.hrs.service;

import org.springframework.stereotype.Service;
import rom.hrs.dto.CalculationRequest;
import rom.hrs.dto.CalculationResponse;

import java.time.LocalDate;

@Service
public class CalculationService {

    // Тарифные ставки (в реальном проекте должны браться из БД/конфига)
    private static final double INCOMING_RATE = 0.5;  // руб/мин для входящих
    private static final double OUTGOING_RATE = 1.5;  // руб/мин для исходящих
    private static final double MONTHLY_FEE = 100.0; // абонплата

    /**
     * Основной метод расчета стоимости звонка
     */
    public CalculationResponse calculate(CalculationRequest request) {
        // Для примера: тариф 11 - помесячный, 12 - поминутный
        if (request.tariffId() == 11) {
            return handleMonthlyTariff(request);
        } else {
            return handlePayAsYouGoTariff(request);
        }
    }

    /**
     * Обработка помесячного тарифа
     */
    private CalculationResponse handleMonthlyTariff(CalculationRequest request) {
        // Упрощенная логика: первые 100 минут бесплатно
        final int includedMinutes = 100;
        int remainingMinutes = includedMinutes - getUsedMinutes(request.caller().msisdn());

        if (remainingMinutes >= request.durationMinutes()) {
            return CalculationResponse.forPackageMinutes(
                    remainingMinutes - request.durationMinutes(),
                    "Использованы минуты пакета"
            );
        } else {
            double cost = request.durationMinutes() * getRate(request.callType());
            return CalculationResponse.forPerMinute(
                    cost,
                    "Лимит пакета исчерпан, применена поминутная тарификация"
            );
        }
    }

    /**
     * Обработка поминутного тарифа
     */
    private CalculationResponse handlePayAsYouGoTariff(CalculationRequest request) {
        double cost = request.durationMinutes() * getRate(request.callType());
        return CalculationResponse.forPerMinute(
                cost,
                "Поминутная тарификация"
        );
    }

    /**
     * Расчет абонентской платы
     */
    public CalculationResponse calculateMonthlyFee(String msisdn, int tariffId) {
        LocalDate nextPayment = LocalDate.now().plusMonths(1);
        return CalculationResponse.forMonthlyFee(
                MONTHLY_FEE,
                nextPayment
        );
    }

    private double getRate(String callType) {
        return callType.equals("01") ? OUTGOING_RATE : INCOMING_RATE;
    }

    private int getUsedMinutes(String msisdn) {
        // В реальной реализации - запрос к БД
        return 60; // Пример: уже использовано 60 минут
    }
}
