package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TariffParamDto(
        @NotBlank String name,
        String description,
        Double value,
        String units
) {}
