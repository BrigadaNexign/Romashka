package rom.crm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EXTERNAL_SERVICE("PS-001", "External service failed", 500),
    TARIFF_NOT_FOUND("PS-002", "Tariff not found", 500);

    private final String code;
    private final String description;
    private final int httpStatus;
}

