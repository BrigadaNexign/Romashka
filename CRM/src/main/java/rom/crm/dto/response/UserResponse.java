package rom.crm.dto.response;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о пользователе.
 *
 * @param id ID пользователя
 * @param name Имя пользователя
 * @param msisdn Номер телефона
 * @param tariffId ID текущего тарифа
 * @param balance Текущий баланс
 * @param minutesRemaining Оставшиеся минуты
 * @param regDate Дата регистрации
 * @param nextPay Дата следующего платежа
 */public record UserResponse(
        Long id,
        String name,
        String msisdn,
        Long tariffId,
        Double balance,
        Integer minutesRemaining,
        LocalDateTime regDate,
        LocalDateTime nextPay
) { }
