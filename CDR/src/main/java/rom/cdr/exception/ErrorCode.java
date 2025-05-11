package rom.cdr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EMPTY_FIELD("RP-001", "One of the fields is empty"),
    CONFLICTING_CALL("FG-001", "Generated call has conflict");

    private final String code;
    private final String description;
}
