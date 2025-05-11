package rom.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для создания/обновления абонента
 *
 * @param name (обязательное) Имя абонента
 * @param msisdn (обязательное) Номер телефона (формат: ^[7-8]\\d{10}$)
 * @param tariffId (обязательное) ID тарифного плана
 * @param balance (опциональное) Начальный баланс (по умолчанию 100.0)
 * @param minutes (опциональное) Доступные минуты (по умолчанию 0)
 * @param paymentDay (опциональное) Дата следующего платежа (по умолчанию: текущая дата + 1 месяц)
 */
public record UserUpdateRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^[7-8]\\d{10}$") String msisdn,
        @NotNull Long tariffId,
        Double balance,
        Integer minutes,
        LocalDate paymentDay
) {
    public UserUpdateRequest {
        balance = balance != null ? balance : 100.0;
        minutes = minutes != null ? minutes : 0;
    }
}
