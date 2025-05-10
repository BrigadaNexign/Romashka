package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

// Запрос на смену тарифа
public record ChangeTariffRequest(
        @NotNull Long tariffId
) {}

