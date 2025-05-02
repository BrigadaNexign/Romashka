package rom.hrs.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    private Double cost;
    private String tariffType;
    private String description;
    private Integer remainingMinutes;
    private LocalDate nextPaymentDate;
}
