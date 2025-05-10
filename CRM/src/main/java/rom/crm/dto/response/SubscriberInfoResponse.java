package rom.crm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberInfoResponse {
    private UserResponse user;
    private TariffResponse tariff;
}