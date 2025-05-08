package rom.brt.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    private boolean success;
    private Double cost;
    private String tariffType;
    private String description;
    private Integer remainingMinutes;
    private LocalDate nextPaymentDate;
    private String errorCode;
    private String errorMessage;
}
