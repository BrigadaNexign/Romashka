package rom.hrs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import rom.hrs.exception.InvalidPricingTypeException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@ToString
public enum PricingType {
    OUTGOING_BOTH_SERVICED(1, "Outgoing call, both parties serviced"),
    OUTGOING_CALLER_SERVICED(2, "Outgoing call, only caller serviced"),
    INCOMING_BOTH_SERVICED(3, "Incoming call, both serviced"),
    INCOMING_CALLER_SERVICED(4, "Incoming call, only caller serviced");

    private final int code;
    private final String description;

    public static PricingType fromCode(int code) throws InvalidPricingTypeException {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new InvalidPricingTypeException(code));
    }
}
