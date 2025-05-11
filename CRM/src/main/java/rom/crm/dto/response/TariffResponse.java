package rom.crm.dto.response;

import java.util.List;

// Ответ с информацией о тарифе
public record TariffResponse(
        Long id,
        String name,
        String description,
        Integer intervalDays,
        Double price,
        Integer type,
        List<CallPriceResponse> callPrices,
        List<TariffParamResponse> params
) {}
