package rom.crm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с полной информацией об абоненте.
 * Содержит данные пользователя и его тарифа.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberInfoResponse {
    private UserResponse user;
    private TariffResponse tariff;
}