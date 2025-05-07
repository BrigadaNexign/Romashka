package rom.hrs.exception;

import lombok.Getter;

@Getter
public class NoPricingsFoundException extends BusinessException {
    private final Long tariffId;
    private final ErrorCode errorCode;

    public NoPricingsFoundException(Long tariffId) {
        super(String.format(
                "%s: %s Tariff id: %d",
                ErrorCode.NO_PRICINGS_FOUND.getCode(),
                ErrorCode.NO_PRICINGS_FOUND.getDescription(),
                tariffId
        ));
        this.tariffId = tariffId;
        this.errorCode = ErrorCode.NO_PRICINGS_FOUND;
    }
}
