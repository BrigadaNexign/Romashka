package rom.hrs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

// Запрос на создание тарифа
public record CreateTariffRequest(
        @NotBlank String name,
        String description,
        @NotNull @Positive Integer intervalDays,
        @NotNull @Positive Double price,
        List<CallPriceDto> callPrices,
        List<TariffParamDto> params
) {}
