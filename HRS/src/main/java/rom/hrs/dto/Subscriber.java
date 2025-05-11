package rom.hrs.dto;

import java.time.LocalDate;

/**
 * Представляет абонента.
 * Может быть как обслуживаемым (с тарифом), так и внешним (без тарифа).
 */
public record Subscriber(
        Integer id,
        String msisdn,
        boolean isServiced,
        Long tariffId,
        Integer minutes,
        LocalDate paymentDay
) {
    public Subscriber {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Invalid id. Id must be positive or none");
        }
        if (msisdn.length() != 11) {
            throw new IllegalArgumentException("Invalid msisdn. Msisdn length must be equal 11");
        }
        if (id == null && isServiced) {
            throw new IllegalStateException("Subscriber without id cannot be serviced");
        }
    }
}