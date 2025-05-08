package rom.brt.exception;

import lombok.Getter;

@Getter
public class EmptyResponseFieldException extends BusinessException {
    private final ErrorCode errorCode;
    public EmptyResponseFieldException(String field) {
        super(String.format("%s: %s: %s",
                ErrorCode.EMPTY_RESPONSE.getCode(),
                ErrorCode.EMPTY_RESPONSE.getDescription(),
                field
        ));
        this.errorCode = ErrorCode.EMPTY_RESPONSE;
    }
}
