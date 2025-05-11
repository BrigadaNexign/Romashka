package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для параметра тарифа.
 *
 * @param name Название параметра (обязательное поле)
 * @param description Описание параметра
 * @param value Значение параметра
 * @param units Единицы измерения параметра
 */
public record TariffParamDto(
        @NotBlank String name,
        String description,
        Double value,
        String units
) {}
