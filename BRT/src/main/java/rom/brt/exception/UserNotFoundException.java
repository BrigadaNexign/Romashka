package rom.brt.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends BusinessException {
    private final ErrorCode errorCode;
    public UserNotFoundException(String msisdn) {
        super(String.format("%s: %s Msisdn: %s",
                ErrorCode.USER_NOT_FOUND.getCode(),
                ErrorCode.USER_NOT_FOUND.getDescription(),
                msisdn
        ));
        this.errorCode = ErrorCode.USER_NOT_FOUND;
    }
}
