package rom.hrs.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor()
@AllArgsConstructor
public class CalculationResponse {
    @Builder.Default
    private Double cost = 0.0;
    @Builder.Default
    private String tariffType = "00";
    @Builder.Default
    private String description = "Default description";
    private Integer remainingMinutes ;
    private LocalDate nextPaymentDate;
}
