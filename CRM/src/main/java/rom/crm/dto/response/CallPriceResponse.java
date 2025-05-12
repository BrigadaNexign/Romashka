package rom.crm.dto.response;

/**
 * DTO для ответа с информацией о цене звонка.
 *
 * @param callType Тип звонка
 * @param pricePerMinute Цена за минуту
 */
public record CallPriceResponse(
        Integer callType,
        Double pricePerMinute
) {}
