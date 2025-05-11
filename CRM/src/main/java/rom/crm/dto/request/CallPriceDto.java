package rom.crm.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO для цены звонка определенного типа.
 *
 * @param callType Тип звонка (1 - исходящий, 2 - входящий и т.д.)
 * @param pricePerMinute Цена за минуту (должна быть положительной)
 */
public record CallPriceDto(
        @NotNull Integer callType,
        @NotNull @Positive Double pricePerMinute
) {}
