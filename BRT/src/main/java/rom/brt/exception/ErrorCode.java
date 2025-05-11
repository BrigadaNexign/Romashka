package rom.brt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PARSING("MH-001", "Failed to parse CSV", 500),
    EMPTY_RESPONSE("RH-001", "One of the fields in HRS response is empty", 500),
    FAILED_RESPONSE("RH-002", "Calculation in HRS failed", 500),
    USER_NOT_FOUND("US-001", "Usern not found", 400);

    private final String code;
    private final String description;
    private final int httpStatus;
}

