package rom.crm.dto.response;

/**
 * DTO для ответа с информацией о параметре тарифа.
 *
 * @param name Название параметра
 * @param description Описание параметра
 * @param value Значение параметра
 * @param units Единицы измерения
 */
public record TariffParamResponse(
        String name,
        String description,
        Double value,
        String units
) {}
