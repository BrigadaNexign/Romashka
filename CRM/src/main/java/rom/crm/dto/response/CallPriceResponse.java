package rom.crm.dto.response;

public record CallPriceResponse(
        Integer callType,
        Double pricePerMinute
) {}
