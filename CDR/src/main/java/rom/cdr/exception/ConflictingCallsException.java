package rom.cdr.exception;

import lombok.Getter;

@Getter
public class ConflictingCallsException extends GenerationException {
    private final ErrorCode errorCode;
    public ConflictingCallsException(String conflictDescription) {
        super(String.format(
                "%s: %s Conflict %s",
                ErrorCode.CONFLICTING_CALL.getCode(),
                ErrorCode.CONFLICTING_CALL.getDescription(),
                conflictDescription
        ));
        this.errorCode = ErrorCode.CONFLICTING_CALL;
    }
}
