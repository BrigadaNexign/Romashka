package rom.crm.exception;

import lombok.Getter;

/**
 * Исключение при отсутствии запрошенного тарифного плана.
 */
@Getter
public class TariffNotFoundException extends BusinessException {
    private final ErrorCode errorCode;
    public TariffNotFoundException(String message) {
        super(String.format("%s: %s Message: %s",
                ErrorCode.TARIFF_NOT_FOUND.getCode(),
                ErrorCode.TARIFF_NOT_FOUND.getDescription(),
                message
        ));
        this.errorCode = ErrorCode.TARIFF_NOT_FOUND;
    }
}
