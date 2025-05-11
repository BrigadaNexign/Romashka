package rom.crm.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

// Запрос на смену тарифа
public record ChangeTariffRequest(
        @NotNull Long tariffId
) {}

