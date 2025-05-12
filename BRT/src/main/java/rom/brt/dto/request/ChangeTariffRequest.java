package rom.brt.dto.request;

import jakarta.validation.constraints.NotNull;

// Запрос на смену тарифа
public record ChangeTariffRequest(
        @NotNull Long tariffId
) {}

