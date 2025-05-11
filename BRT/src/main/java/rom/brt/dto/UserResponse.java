package rom.brt.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String userName;
    private Long tariffId;
    private String msisdn;
    private BigDecimal balance;
    private LocalDateTime registrationDate;
}
