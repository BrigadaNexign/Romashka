package rom.crm.dto.response;

/**
 * DTO для ответа с информацией о балансе.
 *
 * @param msisdn Номер телефона абонента
 * @param amount Текущий баланс
 */public record BalanceResponse(
        String msisdn,
        Double amount
) {}
