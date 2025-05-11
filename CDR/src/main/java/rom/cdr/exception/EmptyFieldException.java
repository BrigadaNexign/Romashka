package rom.cdr.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при отсутствии обязательных полей.
 */
@Getter
public class EmptyFieldException extends GenerationException {
    private final ErrorCode errorCode;
    /**
     * Создает исключение с указанием отсутствующего поля.
     *
     * @param missingField название отсутствующего поля
     */
    public EmptyFieldException(String missingField) {
        super(String.format(
                "%s: %s Empty field: %s",
                ErrorCode.EMPTY_FIELD.getCode(),
                ErrorCode.EMPTY_FIELD.getDescription(),
                missingField
        ));
        this.errorCode = ErrorCode.EMPTY_FIELD;
    }
}
