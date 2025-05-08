package rom.cdr.exceptions;

import lombok.Getter;

@Getter
public class EmptyFieldException extends GenerationException {
    private final ErrorCode errorCode;
    public EmptyFieldException(String missingField) {
        super(String.format(
                "%s: %s Empty field: %s",
                ErrorCode.EMPTY_FIELD.getCode(),
                ErrorCode.EMPTY_FIELD.getDescription(),
                missingField
        ));
        this.errorCode = ErrorCode.EMPTY_FIELD;
    }
}
