package rom.hrs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rom.hrs.exception.InvalidCallTypeException;

@Getter
@AllArgsConstructor
public enum CallType {
    OUTGOING("01", "Outgoing"),
    INCOMING("02", "Incoming");

    private final String code;
    private final String description;

    public static CallType fromCode(String code) throws InvalidCallTypeException {
        for (CallType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new InvalidCallTypeException(code);
    }
}
