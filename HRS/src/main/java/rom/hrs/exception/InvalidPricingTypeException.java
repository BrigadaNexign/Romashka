package rom.hrs.exception;

import lombok.Getter;
import rom.hrs.entity.PricingType;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class InvalidPricingTypeException extends BusinessException {
    private final int pricingType;
    private final ErrorCode errorCode;

    public InvalidPricingTypeException(int pricingType) {
        super(String.format(
                "%s: %s Pricing type: %s. Valid call types are: %s",
                ErrorCode.INVALID_PRICING_TYPE.getCode(),
                ErrorCode.INVALID_PRICING_TYPE.getDescription(),
                pricingType,
                Arrays.stream(PricingType.values())
                        .mapToInt(PricingType::getCode)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", "))
        ));
        this.pricingType = pricingType;
        this.errorCode = ErrorCode.INVALID_PRICING_TYPE;
    }
}
