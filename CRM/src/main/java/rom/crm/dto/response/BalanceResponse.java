package rom.crm.dto.response;

// Ответ с информацией о балансе
public record BalanceResponse(
        String msisdn,
        Double amount
) {}
