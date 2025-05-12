package rom.cdr.exception;

/**
 * Базовое исключение для ошибок генерации CDR.
 */
public class GenerationException extends Exception {
    private ErrorCode errorCode;

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
