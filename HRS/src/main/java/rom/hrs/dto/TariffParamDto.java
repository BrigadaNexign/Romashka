package rom.hrs.dto;

import jakarta.validation.constraints.NotBlank;

public record TariffParamDto(
        @NotBlank String name,
        String description,
        Double value,
        String units
) {}
