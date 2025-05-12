package rom.brt.dto.request;

import lombok.*;
import rom.brt.dto.Subscriber;

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