package rom.hrs.dto;

import java.util.List;

// Ответ с информацией о тарифе
public record TariffResponse(
        Long id,
        String name,
        String description,
        Integer intervalDays,
        Double price,
        List<CallPriceDto> callPrices,
        List<TariffParamResponse> params
) { }
