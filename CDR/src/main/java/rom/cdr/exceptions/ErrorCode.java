package rom.cdr.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMPTY_FIELD("RP-001", "One of the fields is empty");

    private final String code;
    private final String description;
}
