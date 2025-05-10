package rom.brt.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserResponse {
    private Long userId;
    private String userName;
    private Long tariffId;
    private String msisdn;
    private BigDecimal balance;
    private LocalDateTime registrationDate;
}
