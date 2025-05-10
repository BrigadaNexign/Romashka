package rom.crm.dto.response;

public record TariffParamResponse(
        String name,
        String description,
        Double value,
        String units
) {}
