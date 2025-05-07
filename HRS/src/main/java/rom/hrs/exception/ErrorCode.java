package rom.hrs.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INCOMPLETE_RESPONSE("CS-001", "Some fields in response are null", 500),
    NO_PRICINGS_FOUND("CPS-001", "No pricings found for tariff", 400),
    PRICING_NOT_FOUND("CPS-002", "No pricing found for tariff and call type", 400),
    INVALID_CALL_TYPE("CPS-003", "Invalid call type", 400),
    INVALID_PRICING_TYPE("CPS-004", "Invalid pricing type", 400),
    UNSUPPORTED_SERVICE_COMBINATION("CPS-005", "Unsupported combination of call type and service status", 400),
    NO_TARIFF_FOUND("TC-001", "No tariff found for tariff id", 400),
    NO_INTERVALS_FOUND("TC-002", "No intervals found for tariff", 400),
    UNSUPPORTED_TARIFF_TYPE("TC-003", "Unsupported tariff type", 500);

    private final String code;
    private final String description;
    private final int httpStatus;
}
