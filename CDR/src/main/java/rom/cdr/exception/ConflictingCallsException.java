package rom.cdr.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при обнаружении конфликтующих (пересекающихся по абонентам и времени звонка) вызовов в CDR.
 */
@Getter
public class ConflictingCallsException extends GenerationException {
    private final ErrorCode errorCode;
    /**
     * Создает исключение с описанием конфликта.
     *
     * @param conflictDescription описание конфликтующей ситуации
     */
    public ConflictingCallsException(String conflictDescription) {
        super(String.format(
                "%s: %s Conflict %s",
                ErrorCode.CONFLICTING_CALL.getCode(),
                ErrorCode.CONFLICTING_CALL.getDescription(),
                conflictDescription
        ));
        this.errorCode = ErrorCode.CONFLICTING_CALL;
    }
}
