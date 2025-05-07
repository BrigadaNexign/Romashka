package rom.hrs.exception;

import lombok.Getter;
import rom.hrs.entity.CallType;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class InvalidCallTypeException extends BusinessException {
    private final String callType;
    private final ErrorCode errorCode;

    public InvalidCallTypeException(String callType) {
        super(String.format(
                "%s: %s Call type: %s. Valid call types are: %s",
                ErrorCode.INVALID_CALL_TYPE.getCode(),
                ErrorCode.INVALID_CALL_TYPE.getDescription(),
                callType,
                Arrays.stream(CallType.values())
                        .map(CallType::getCode)
                        .collect(Collectors.joining(", "))
        ));
        this.callType = callType;
        this.errorCode = ErrorCode.INVALID_CALL_TYPE;
    }
}
