package rom.brt.dto;

import java.time.LocalDate;

public record CalculationRequest(
        String callType,
        Subscriber caller,
        Subscriber receiver,
        int durationMinutes,
        LocalDate currentDate
) {
    public CalculationRequest {
        if (!callType.matches("01|02")) {
            throw new IllegalArgumentException("Invalid callType. Must be '01' or '02'");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
}
