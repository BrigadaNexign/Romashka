package rom.hrs.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * Результат расчета стоимости звонка.
 */
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
