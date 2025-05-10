package rom.crm.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CallPriceDto(
        @NotNull Integer callType,
        @NotNull @Positive Double pricePerMinute
) {}
