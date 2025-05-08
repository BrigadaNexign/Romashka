package rom.brt.exception;

import lombok.Getter;

@Getter
public class FailedResponseException extends BusinessException {
    private final ErrorCode errorCode;
    private final String receivedErrorCode;
    private final String receivedErrorMessage;

    public FailedResponseException(String code, String message) {
        super(String.format(
                "%s: %s Code %s Message %s",
                ErrorCode.FAILED_RESPONSE.getCode(),
                ErrorCode.FAILED_RESPONSE.getDescription(),
                code,
                message
                ));
        this.errorCode = ErrorCode.FAILED_RESPONSE;
        this.receivedErrorCode = code;
        this.receivedErrorMessage = message;
    }
}
