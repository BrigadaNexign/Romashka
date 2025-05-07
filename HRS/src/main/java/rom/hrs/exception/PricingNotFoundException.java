package rom.hrs.exception;

import lombok.Getter;
import rom.hrs.entity.PricingType;

@Getter
public class PricingNotFoundException extends BusinessException {
    private final Long tariffId;
    private final PricingType pricingType;
    private final ErrorCode errorCode;

    public PricingNotFoundException(Long tariffId, PricingType pricingType) {
        super(String.format(
                "%s: %s Tariff id: %d Call type id: %S",
                ErrorCode.PRICING_NOT_FOUND.getCode(),
                ErrorCode.PRICING_NOT_FOUND.getDescription(),
                tariffId,
                pricingType.toString()
        ));
        this.tariffId = tariffId;
        this.pricingType = pricingType;
        this.errorCode = ErrorCode.PRICING_NOT_FOUND;
    }
}
