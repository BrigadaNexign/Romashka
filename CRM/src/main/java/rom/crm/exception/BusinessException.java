package rom.crm.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends Exception {
    private ErrorCode errorCode;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
