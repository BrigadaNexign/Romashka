package rom.hrs.exception;

import lombok.Getter;

@Getter
public class NoIntervalsFoundException extends BusinessException {
    private final Long tariffId;
    private final ErrorCode errorCode;

    public NoIntervalsFoundException(Long tariffId) {
        super(String.format(
                "%s: %s Tariff id: %d",
                ErrorCode.NO_INTERVALS_FOUND.getCode(),
                ErrorCode.NO_INTERVALS_FOUND.getDescription(),
                tariffId));
        this.tariffId = tariffId;
        this.errorCode = ErrorCode.NO_INTERVALS_FOUND;
    }
}
