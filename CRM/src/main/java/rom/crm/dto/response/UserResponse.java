package rom.crm.dto.response;

import java.time.LocalDateTime;

// Ответ с информацией о пользователе
public record UserResponse(
        Long id,
        String name,
        String msisdn,
        Long tariffId,
        Double balance,
        Integer minutesRemaining,
        LocalDateTime regDate,
        LocalDateTime nextPay
) { }
