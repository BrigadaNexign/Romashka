package rom.brt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String msisdn;
    private Long tariffId;
    private BigDecimal balance;
    private Integer minutesRemaining;
    private LocalDateTime regDate;
    private LocalDate nextPay;
}
