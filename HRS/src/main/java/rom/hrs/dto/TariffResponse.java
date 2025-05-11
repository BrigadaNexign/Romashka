package rom.hrs.dto;

import java.util.List;

/**
 * DTO для ответа с информацией о тарифном плане.
 *
 * @param id ID тарифа
 * @param name Название тарифа
 * @param description Описание тарифа
 * @param intervalDays Интервал оплаты в днях
 * @param price Стоимость тарифа
 * @param type Тип тарифа
 * @param callPrices Список цен для разных типов звонков
 * @param params Дополнительные параметры тарифа
 */
public record TariffResponse(
        Long id,
        String name,
        String description,
        Integer intervalDays,
        Double price,
        Integer type,
        List<CallPriceDto> callPrices,
        List<TariffParamResponse> params
) { }
