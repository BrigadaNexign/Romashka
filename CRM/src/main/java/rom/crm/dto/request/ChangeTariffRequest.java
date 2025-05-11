package rom.crm.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO для запроса смены тарифного плана абонента.
 *
 * @param tariffId ID нового тарифного плана (не может быть null)
 */
public record ChangeTariffRequest(
        @NotNull Long tariffId
) {}

