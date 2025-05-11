package rom.hrs.dto;

import jakarta.validation.constraints.NotNull;

// Запрос на смену тарифа
public record ChangeTariffRequest(
        @NotNull Long tariffId
) {}

