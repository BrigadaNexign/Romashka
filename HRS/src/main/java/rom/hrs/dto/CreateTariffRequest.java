package rom.hrs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * DTO для создания нового тарифа.
 *
 * @param name Название тарифа (обязательное поле)
 * @param description Описание тарифа
 * @param intervalDays Интервал оплаты в днях (должен быть положительным)
 * @param price Стоимость тарифа (должна быть положительной)
 * @param callPrices Список цен для разных типов звонков
 * @param params Дополнительные параметры тарифа
 */
public record CreateTariffRequest(
        @NotBlank String name,
        String description,
        @NotNull @Positive Integer intervalDays,
        @NotNull @Positive Double price,
        List<CallPriceDto> callPrices,
        List<TariffParamDto> params
) {}
