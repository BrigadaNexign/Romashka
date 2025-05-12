package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * DTO для запроса пополнения баланса абонента.
 *
 * @param msisdn Номер телефона абонента (должен начинаться с 7 или 8, содержать 11 цифр)
 * @param amount Сумма пополнения (должна быть положительной)
 */
public record BalanceUpdate(
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        @NotNull @Positive Double amount
) {}
