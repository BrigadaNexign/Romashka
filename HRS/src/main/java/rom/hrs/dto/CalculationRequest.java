package rom.hrs.dto;

import lombok.*;
import rom.hrs.entity.CallType;
import rom.hrs.exception.InvalidCallTypeException;

import java.time.LocalDate;

/**
 * Запрос на расчет стоимости звонка.
 */
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

    public CallType getCallTypeAsEnum() throws InvalidCallTypeException {
        return CallType.fromCode(this.callType);
    }
}