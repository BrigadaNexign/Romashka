package rom.hrs.exception;

import lombok.Getter;

@Getter
public class IncompleteResponseException extends BusinessException{
    private final ErrorCode errorCode;
    public IncompleteResponseException(String... missingFields) {
        super(String.format(
                "%s: %s Null fields: %s",
                ErrorCode.INCOMPLETE_RESPONSE.getCode(),
                ErrorCode.INCOMPLETE_RESPONSE.getDescription(),
                String.join(", ", missingFields)
        ));
        this.errorCode = ErrorCode.INCOMPLETE_RESPONSE;
    }
}
