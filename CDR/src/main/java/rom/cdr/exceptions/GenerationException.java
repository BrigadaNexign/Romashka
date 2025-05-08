package rom.cdr.exceptions;

import lombok.Getter;

public class GenerationException extends Exception {
    private ErrorCode errorCode;

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
