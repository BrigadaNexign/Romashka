package rom.crm.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO для цены звонка определенного типа.
 *
 * @param callType Тип звонка (
 *                 1 - Исходящий абоненту Ромашки,
 *                 2 - Исходящий другому абоненту,
 *                 3 - Входящий от абонента Ромашки,
 *                 4 - Входящий от другого абонента
 *                 )
 * @param pricePerMinute Цена за минуту (должна быть положительной)
 */
public record CallPriceDto(
        @NotNull Integer callType,
        @NotNull @Positive Double pricePerMinute
) {}
