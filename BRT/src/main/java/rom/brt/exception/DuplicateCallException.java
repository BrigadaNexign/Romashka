package rom.brt.exception;

import lombok.Getter;

@Getter
public class DuplicateCallException extends BusinessException {
    private final ErrorCode errorCode;
    public DuplicateCallException(String fragment) {
        super(String.format(
                "%s: %s Call: %s",
                ErrorCode.DUPLICATE_CALL.getCode(),
                ErrorCode.DUPLICATE_CALL.getDescription(),
                fragment
        ));
        this.errorCode = ErrorCode.DUPLICATE_CALL;
    }
}
