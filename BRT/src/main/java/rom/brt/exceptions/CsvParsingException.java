package rom.brt.exceptions;

import lombok.Getter;

@Getter
public class CsvParsingException extends BusinessException {
    private final ErrorCode errorCode;
    public CsvParsingException(Throwable cause) {
        super(String.format("%s: %s: %s",
                ErrorCode.PARSING.getCode(),
                ErrorCode.PARSING.getDescription(),
                cause.getMessage()
        ));
        this.errorCode = ErrorCode.PARSING;
    }
}
