package rom.brt.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationRequest {
    private String callType;
    private Subscriber caller;
    private Subscriber receiver;
    private int durationMinutes;
    private LocalDate currentDate;
}