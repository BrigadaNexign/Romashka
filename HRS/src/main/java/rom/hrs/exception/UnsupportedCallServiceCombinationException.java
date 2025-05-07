package rom.hrs.exception;

import lombok.Getter;
import rom.hrs.entity.CallType;

@Getter
public class UnsupportedCallServiceCombinationException extends BusinessException {
    private final CallType callType;
    private final boolean isCallerServiced;
    private final boolean isReceiverServiced;
    private final ErrorCode errorCode = ErrorCode.UNSUPPORTED_SERVICE_COMBINATION;

    public UnsupportedCallServiceCombinationException(CallType callType,
                                                      boolean isCallerServiced,
                                                      boolean isReceiverServiced) {
        super(String.format(
                "%s: %s CallType: %s Caller serviced: %s, Receiver serviced: %s",
                ErrorCode.UNSUPPORTED_SERVICE_COMBINATION.getCode(),
                ErrorCode.UNSUPPORTED_SERVICE_COMBINATION.getDescription(),
                callType.getDescription(),
                isCallerServiced,
                isReceiverServiced
        ));
        this.callType = callType;
        this.isCallerServiced = isCallerServiced;
        this.isReceiverServiced = isReceiverServiced;
    }
}
