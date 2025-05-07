package rom.hrs.exception;

import lombok.Getter;

@Getter
public class UnsupportedTariffTypeException extends BusinessException {
    private final Integer tariffType;
    private final ErrorCode errorCode;

    public UnsupportedTariffTypeException(Integer tariffType) {
        super(String.format(
                "%s: %s Tariff type: %d",
                ErrorCode.UNSUPPORTED_TARIFF_TYPE.getCode(),
                ErrorCode.UNSUPPORTED_TARIFF_TYPE.getDescription(),
                tariffType));
        this.tariffType = tariffType;
        this.errorCode = ErrorCode.UNSUPPORTED_TARIFF_TYPE;
    }
}
