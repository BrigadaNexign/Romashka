package rom.hrs.exception;

import lombok.Getter;

@Getter
public class NoTariffFoundException extends BusinessException {
    private final Long tariffId;
    private final ErrorCode errorCode;

    public NoTariffFoundException(Long tariffId) {
        super(String.format(
                "%s: %s Tariff id: %d",
                ErrorCode.NO_TARIFF_FOUND.getCode(),
                ErrorCode.NO_TARIFF_FOUND.getDescription(),
                tariffId));
        this.tariffId = tariffId;
        this.errorCode = ErrorCode.NO_TARIFF_FOUND;

    }
}
