package rom.crm.exception;

import lombok.Getter;

/**
 * Исключение при ошибках взаимодействия с внешними сервисами.
 */
@Getter
public class ExternalServiceException extends BusinessException {
    private final ErrorCode errorCode;
    public ExternalServiceException(String message) {
        super(String.format("%s: %s Message: %s",
                ErrorCode.EXTERNAL_SERVICE.getCode(),
                ErrorCode.EXTERNAL_SERVICE.getDescription(),
                message
        ));
        this.errorCode = ErrorCode.EXTERNAL_SERVICE;
    }
}
